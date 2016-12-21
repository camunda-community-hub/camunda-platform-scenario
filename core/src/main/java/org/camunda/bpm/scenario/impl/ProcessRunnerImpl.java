package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstantiationBuilder;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.impl.util.Api;
import org.camunda.bpm.scenario.impl.util.IdComparator;
import org.camunda.bpm.scenario.impl.util.Log;
import org.camunda.bpm.scenario.impl.util.Log.Action;
import org.camunda.bpm.scenario.impl.waitstate.CallActivityExecutable;
import org.camunda.bpm.scenario.run.ProcessRunner;
import org.camunda.bpm.scenario.run.ProcessRunner.ExecutableRunner.StartingByKey;
import org.camunda.bpm.scenario.run.ProcessRunner.ExecutableRunner.StartingByMessage;
import org.camunda.bpm.scenario.run.ProcessRunner.ExecutableRunner.StartingByStarter;
import org.camunda.bpm.scenario.run.ProcessRunner.StartableRunner;
import org.camunda.bpm.scenario.run.ProcessStarter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ProcessRunnerImpl extends AbstractRunner implements StartingByKey, StartingByMessage, StartableRunner, StartingByStarter, ProcessRunner {

  private String startMessage;
  private ProcessStarter processStarter;
  private Map<String, Object> variables;
  private Map<String, Boolean> fromActivityIds = new HashMap<String, Boolean>();

  private Set<String> executed = new HashSet<String>();
  private Set<String> started = new HashSet<String>();
  private Set<String> finished = new HashSet<String>();

  ScenarioImpl scenarioExecutor;
  ProcessScenario scenario;
  ProcessInstance processInstance;
  String processDefinitionKey;

  public ProcessRunnerImpl(ScenarioImpl scenarioExecutor, ProcessScenario scenario) {
    this.scenarioExecutor = scenarioExecutor;
    this.scenario = scenario;
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
    scenarioExecutor.runners.add(new ProcessRunnerImpl(scenarioExecutor, scenario));
    return scenarioExecutor.toBeStartedBy();
  }

  @Override
  public ExecutableRunner engine(ProcessEngine processEngine) {
    scenarioExecutor.init(processEngine);
    return this;
  }

  @Override
  public Scenario execute() {
    return scenarioExecutor.execute();
  }

  public ProcessEngine engine() {
    return scenarioExecutor.processEngine;
  }

  public String getProcessDefinitionKey() {
    return processDefinitionKey;
  }

  public void running(CallActivityExecutable waitstate) {
    this.scenarioExecutor = waitstate.runner.scenarioExecutor;
    this.scenarioExecutor.runners.add(this);
    this.processInstance = waitstate;
    processDefinitionKey = engine().getRepositoryService().createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).singleResult().getKey();
    setExecuted();
  }

  @SuppressWarnings("deprecation")
  public ProcessInstance run() {
    if (this.processInstance == null) {
      if (this.processDefinitionKey != null) {
        this.processStarter = new ProcessStarter() {
          @Override
          public ProcessInstance start() {
            if (fromActivityIds.isEmpty()) {
              return scenarioExecutor.processEngine.getRuntimeService().startProcessInstanceByKey(processDefinitionKey, variables);
            } else {
              ProcessInstantiationBuilder builder = scenarioExecutor.processEngine.getRuntimeService().createProcessInstanceByKey(processDefinitionKey);
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
            return scenarioExecutor.processEngine.getRuntimeService().startProcessInstanceByMessage(startMessage, variables);
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
    List<Executable> executables = new ArrayList<Executable>();
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
    List<HistoricActivityInstance> instances = scenarioExecutor.processEngine.getHistoryService()
        .createHistoricActivityInstanceQuery().processInstanceId(processInstance.getId()).list();
    Collections.sort(instances, new Comparator<HistoricActivityInstance>() {
      IdComparator idComparator = new IdComparator();
      @Override
      public int compare(HistoricActivityInstance instance1, HistoricActivityInstance instance2) {
        return idComparator.compare(instance1.getId(), instance2.getId());
      }
    });
    for (HistoricActivityInstance instance: instances) {
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
        scenario.hasStarted(instance.getActivityId());
        started.add(instance.getId());
      }
      if (instance.getEndTime() != null && !finished.contains(instance.getId())) {
        scenario.hasFinished(instance.getActivityId());
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
            scenario.hasCanceled(instance.getActivityId());
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
            scenario.hasCompleted(instance.getActivityId());
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

  public void setExecuted(WaitstateExecutable waitstate) {
    executed.add(waitstate.historicDelegate.getId());
    setExecuted();
  }

  public boolean isExecuted(HistoricActivityInstance instance) {
    return executed.contains(instance.getId());
  }

}
