package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstantiationBuilder;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.impl.util.Api;
import org.camunda.bpm.scenario.impl.util.IdComparator;
import org.camunda.bpm.scenario.impl.util.Log;
import org.camunda.bpm.scenario.impl.waitstate.CallActivityExecutable;
import org.camunda.bpm.scenario.run.ProcessRunner;
import org.camunda.bpm.scenario.run.ProcessRunner.ExecutableRunner.StartingByKey;
import org.camunda.bpm.scenario.run.ProcessRunner.ExecutableRunner.StartingByMessage;
import org.camunda.bpm.scenario.run.ProcessRunner.ExecutableRunner.StartingByStarter;
import org.camunda.bpm.scenario.run.ProcessRunner.StartableRunner;
import org.camunda.bpm.scenario.run.ProcessStarter;

import java.util.*;

/**
 * @author Martin Schimak
 */
public class ProcessInstanceRunner extends AbstractRunner implements ProcessRunner,
  StartingByKey, StartingByMessage, StartableRunner, StartingByStarter {

  private final Map<String, Boolean> fromActivityIds = new HashMap<>();
  private final Set<String> executed = new HashSet<>();
  private final Set<String> started = new HashSet<>();
  private final Set<String> finished = new HashSet<>();

  ScenarioRunner scenarioRunner;
  ProcessScenario processScenario;
  ProcessInstance processInstance;
  String processDefinitionKey;
  String businessKey;

  private String startMessage;
  private ProcessStarter processStarter;
  private Map<String, Object> variables;

  public ProcessInstanceRunner(ScenarioRunner scenarioRunner, ProcessScenario processScenario) {
    this.scenarioRunner = scenarioRunner;
    this.processScenario = processScenario;
  }

  @Override
  public StartingByStarter startBy(ProcessStarter scenarioStarter) {
    this.processStarter = scenarioStarter;
    return this;
  }

  @Override
  public StartingByKey startByKey(String processDefinitionKey) {
    this.processDefinitionKey = processDefinitionKey;
    return this;
  }

  @Override
  public StartingByKey startByKey(String processDefinitionKey, Map<String, Object> variables) {
    this.processDefinitionKey = processDefinitionKey;
    this.variables = variables;
    return this;
  }

  @Override
  public StartingByKey startByKey(String processDefinitionKey, String businessKey, Map<String, Object> variables) {
    this.processDefinitionKey = processDefinitionKey;
    this.variables = variables;
    this.businessKey = businessKey;
    return this;
  }

  @Override
  public StartingByKey startByKey(String processDefinitionKey, String businessKey) {
    return startByKey(processDefinitionKey, businessKey, null);
  }

  @Override
  public StartingByMessage startByMessage(String messageName) {
    this.startMessage = messageName;
    return this;
  }

  @Override
  public StartingByMessage startByMessage(String messageName, Map<String, Object> variables) {
    this.startMessage = messageName;
    this.variables = variables;
    return this;
  }

  @Override
  public StartingByKey fromBefore(String activityId) {
    Api.feature(RuntimeService.class.getName(), "createProcessInstanceByKey", String.class)
      .fail("Outdated Camunda BPM version used will not allow to start process instances " +
        "at explicitely selected activity IDs");
    fromActivityIds.put(activityId, true);
    return this;
  }

  @Override
  public StartingByKey fromAfter(String activityId) {
    Api.feature(RuntimeService.class.getName(), "createProcessInstanceByKey", String.class)
      .fail("Outdated Camunda BPM version used will not allow to start process instances " +
        "at explicitely selected activity IDs");
    fromActivityIds.put(activityId, false);
    return this;
  }

  @Override
  public StartableRunner run(ProcessScenario scenario) {
    scenarioRunner.runners.add(new ProcessInstanceRunner(scenarioRunner, scenario));
    return scenarioRunner.toBeStartedBy();
  }

  @Override
  public ExecutableRunner engine(ProcessEngine processEngine) {
    scenarioRunner.init(processEngine);
    return this;
  }

  @Override
  public StartableRunner withMockedProcess(String processDefinitionKey) {
    BpmnModelInstance mockedProcess = Bpmn
      .createProcess(processDefinitionKey)
      .executable()
      .startEvent()
      .userTask()
      .endEvent()
      .done();
    scenarioRunner.mockedProcesses.add(mockedProcess);
    return this;
  }

  @Override
  public Scenario execute() {
    return scenarioRunner.execute();
  }

  public ProcessEngine engine() {
    return scenarioRunner.processEngine;
  }

  public String getProcessDefinitionKey() {
    return processDefinitionKey;
  }

  public void running(CallActivityExecutable executable) {
    this.scenarioRunner = executable.runner.scenarioRunner;
    this.scenarioRunner.runners.add(this);
    this.processInstance = executable;
    processDefinitionKey = engine().getRepositoryService().createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).singleResult().getKey();
    setExecuted();
  }

  public ProcessInstance run() {
    if (this.processInstance == null) {
      if (this.processDefinitionKey != null) {
        this.processStarter = new ProcessStarter() {
          @Override
          public ProcessInstance start() {
            if (fromActivityIds.isEmpty()) {
              return scenarioRunner.processEngine.getRuntimeService()
                .startProcessInstanceByKey(processDefinitionKey, businessKey, variables);
            } else {
              ProcessInstantiationBuilder builder = scenarioRunner.processEngine.getRuntimeService().createProcessInstanceByKey(processDefinitionKey);
              for (String activityId : fromActivityIds.keySet()) {
                Boolean from = fromActivityIds.get(activityId);
                if (from) {
                  builder.startBeforeActivity(activityId);
                } else {
                  builder.startAfterActivity(activityId);
                }
              }
              if (variables != null) {
                builder.setVariables(variables);
              }
              return builder.execute();
            }
          }
        };
      } else if (this.startMessage != null) {
        this.processStarter = new ProcessStarter() {
          @Override
          public ProcessInstance start() {
            return scenarioRunner.processEngine.getRuntimeService().startProcessInstanceByMessage(startMessage, variables);
          }
        };
      }
    }
    if (processInstance == null) {
      this.processInstance = processStarter.start();
      if (processDefinitionKey == null) {
        processDefinitionKey = engine().getRepositoryService().createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).singleResult().getKey();
      }
      setExecuted();
    }
    return this.processInstance;
  }

  @Override
  public List<Executable> next() {
    run();
    List<Executable> executables = new ArrayList<>();
    executables.addAll(Executable.Deferreds.next(this));
    executables.addAll(Executable.Waitstates.next(this));
    executables.addAll(Executable.Jobs.next(this));
    if (executables.isEmpty())
      setExecuted();
    return Executable.Helpers.first(executables);
  }

  public void setExecuted() {
    boolean supportsCanceled = Api.feature(HistoricActivityInstance.class.getName(), "isCanceled")
      .warn("Outdated Camunda BPM version used will not allow to use " +
        "'" + ProcessScenario.class.getName().replace('$', '.') +
        ".hasCanceled(String activityId)' and '.hasCompleted(String activityId)' methods.");
    List<HistoricActivityInstance> instances = scenarioRunner.processEngine.getHistoryService()
      .createHistoricActivityInstanceQuery().processInstanceId(processInstance.getId()).list();
    Collections.sort(instances, new Comparator<HistoricActivityInstance>() {
      final IdComparator idComparator = new IdComparator();

      @Override
      public int compare(HistoricActivityInstance instance1, HistoricActivityInstance instance2) {
        return idComparator.compare(instance1.getId(), instance2.getId());
      }
    });
    for (HistoricActivityInstance instance : instances) {
      if (!started.contains(instance.getId())) {
        Log.Action.Started.log(
          instance.getActivityType(),
          instance.getActivityName(),
          instance.getActivityId(),
          processDefinitionKey,
          instance.getProcessInstanceId(),
          null,
          null
        );
        processScenario.hasStarted(instance.getActivityId());
        started.add(instance.getId());
      }
      if (instance.getEndTime() != null && !finished.contains(instance.getId())) {
        processScenario.hasFinished(instance.getActivityId());
        if (supportsCanceled) {
          if (instance.isCanceled()) {
            Log.Action.Canceled.log(
              instance.getActivityType(),
              instance.getActivityName(),
              instance.getActivityId(),
              processDefinitionKey,
              instance.getProcessInstanceId(),
              null,
              null
            );
            processScenario.hasCanceled(instance.getActivityId());
          } else {
            Log.Action.Completed.log(
              instance.getActivityType(),
              instance.getActivityName(),
              instance.getActivityId(),
              processDefinitionKey,
              instance.getProcessInstanceId(),
              null,
              null
            );
            processScenario.hasCompleted(instance.getActivityId());
          }
        } else {
          Log.Action.Finished.log(
            instance.getActivityType(),
            instance.getActivityName(),
            instance.getActivityId(),
            processDefinitionKey,
            instance.getProcessInstanceId(),
            null,
            null
          );
        }
        finished.add(instance.getId());
      }
    }
  }

  public void setExecuted(WaitstateExecutable<?> executable) {
    executed.add(executable.historicDelegate.getId());
    setExecuted();
  }

  public boolean isExecuted(HistoricActivityInstance instance) {
    return executed.contains(instance.getId());
  }

}
