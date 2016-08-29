package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstantiationBuilder;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.impl.util.Api;
import org.camunda.bpm.scenario.impl.waitstate.CallActivityWaitstate;
import org.camunda.bpm.scenario.runner.ProcessRunner;
import org.camunda.bpm.scenario.runner.ProcessStarter;
import org.camunda.bpm.scenario.runner.ScenarioRun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ProcessRunnerImpl implements ProcessRunner.ExecutableRunner.StartingByKey, ProcessRunner.ToBeStartedBy, ProcessRunner.ExecutableRunner.StartingByStarter, ProcessRunner, Runner {

  private String processDefinitionKey;
  private ProcessStarter processStarter;
  private Map<String, Object> variables;
  private Map<String, Boolean> fromActivityIds = new HashMap<String, Boolean>();

  private Set<String> executed = new HashSet<String>();
  private Set<String> started = new HashSet<String>();
  private Set<String> finished = new HashSet<String>();

  ScenarioExecutorImpl scenarioExecutor;
  Scenario.Process scenario;
  ProcessInstance processInstance;

  public ProcessRunnerImpl(ScenarioExecutorImpl scenarioExecutor, Scenario.Process scenario) {
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
  public ExecutableRunner engine(ProcessEngine processEngine) {
    scenarioExecutor.init(processEngine);
    return this;
  }

  @Override
  public ScenarioRun execute() {
    return scenarioExecutor.execute();
  }

  public ProcessEngine engine() {
    return scenarioExecutor.processEngine;
  }

  public void running(CallActivityWaitstate waitstate) {
    this.scenarioExecutor = waitstate.runner.scenarioExecutor;
    this.scenarioExecutor.runners.add(this);
    this.processInstance = waitstate;
    setExecuted();
  }

  public ProcessInstance run() {
    if (this.processInstance == null && this.processStarter == null) {
      this.processStarter = new ProcessStarter() {
        @Override
        public ProcessInstance start() {
          if (fromActivityIds.isEmpty()) {
            return scenarioExecutor.processEngine.getRuntimeService().startProcessInstanceByKey(processDefinitionKey, variables);
          } else {
            ProcessInstantiationBuilder builder = scenarioExecutor.processEngine.getRuntimeService().createProcessInstanceByKey(processDefinitionKey);
            for (String activityId: fromActivityIds.keySet()) {
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
    } if (processInstance == null) {
      this.processInstance = processStarter.start();
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
          "'" + Scenario.Process.class.getName().replace('$', '.') +
          ".hasCanceled(String activityId)' and '.hasCompleted(String activityId)' methods.");
    List<HistoricActivityInstance> instances = scenarioExecutor.processEngine.getHistoryService()
        .createHistoricActivityInstanceQuery().processInstanceId(processInstance.getId()).list();
    for (HistoricActivityInstance instance: instances) {
      if (!started.contains(instance.getId())) {
        scenario.hasStarted(instance.getActivityId());
        started.add(instance.getId());
      }
      if (instance.getEndTime() != null && !finished.contains(instance.getId())) {
        scenario.hasFinished(instance.getActivityId());
        if (supportsCanceled) {
          if (instance.isCanceled()) {
            scenario.hasCanceled(instance.getActivityId());
          } else {
            scenario.hasCompleted(instance.getActivityId());
          }
        }
        finished.add(instance.getId());
      }
    }
  }

  public void setExecuted(ExecutableWaitstate waitstate) {
    executed.add(waitstate.historicDelegate.getId());
    setExecuted();
  }

  public boolean isExecuted(HistoricActivityInstance instance) {
    return executed.contains(instance.getId());
  }

}
