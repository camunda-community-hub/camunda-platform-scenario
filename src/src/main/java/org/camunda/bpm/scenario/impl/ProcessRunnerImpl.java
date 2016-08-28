package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricActivityInstanceQuery;
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
public class ProcessRunnerImpl implements ProcessRunner.ProcessRunnerStartingByKey, ProcessRunner.ProcessRunnerStartBy, ProcessRunner.ProcessRunnerStartingByStarter, ProcessRunner.CallActivityRunner, Runner {

  private String processDefinitionKey;
  private ProcessStarter processStarter;
  private Map<String, Object> variables;
  private Map<String, Boolean> fromActivityIds = new HashMap<String, Boolean>();
  private Map<String, String> durations = new HashMap<String, String>();

  ScenarioExecutorImpl scenarioExecutor;
  Scenario.Process scenario;
  ProcessInstance processInstance;

  Set<String> unavailable = new HashSet<String>();
  Set<String> started = new HashSet<String>();
  Set<String> executed = new HashSet<String>();

  public ProcessRunnerImpl(ScenarioExecutorImpl scenarioExecutor, Scenario.Process scenario) {
    this.scenarioExecutor = scenarioExecutor;
    this.scenario = scenario;
  }

  @Override
  public ProcessRunnerStartingByStarter startBy(ProcessStarter scenarioStarter) {
    this.processStarter = scenarioStarter;
    return this;
  }

  @Override
  public ProcessRunnerStartingByKey startByKey(String processDefinitionKey) {
    this.processDefinitionKey = processDefinitionKey;
    return this;
  }

  @Override
  public ProcessRunnerStartingByKey startByKey(String processDefinitionKey, Map<String, Object> variables) {
    this.processDefinitionKey = processDefinitionKey;
    this.variables = variables;
    return this;
  }

  @Override
  public ProcessRunnerStartingByKey fromBefore(String activityId) {
    Api.feature(RuntimeService.class.getName(), "createProcessInstanceByKey", String.class)
        .fail("Outdated Camunda BPM version used will not allow to start process instances " +
            "at explicitely selected activity IDs");
    fromActivityIds.put(activityId, true);
    return this;
  }

  @Override
  public ProcessRunnerStartingByKey fromAfter(String activityId) {
    Api.feature(RuntimeService.class.getName(), "createProcessInstanceByKey", String.class)
        .fail("Outdated Camunda BPM version used will not allow to start process instances " +
            "at explicitely selected activity IDs");
    fromActivityIds.put(activityId, false);
    return this;
  }

  @Override
  public ProcessRunner engine(ProcessEngine processEngine) {
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
    setExecuted(null);
  }

  protected ProcessInstance run() {
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
      setExecuted(null);
    }
    return this.processInstance;
  }

  @Override
  public List<Executable> next() {
    run();
    List<Executable> executables = new ArrayList<Executable>();
    executables.addAll(Executable.Jobs.next(this));
    executables.addAll(Executable.Waitstates.next(this));
    if (executables.isEmpty())
      setExecuted(null);
    return Executable.Helpers.first(executables);
  }

  public String getDuration(HistoricActivityInstance instance) {
    if (!durations.containsKey(instance.getId())) {
      durations.put(instance.getId(), scenario.waitsForActionOn(instance.getActivityId()));
    }
    return durations.get(instance.getId());
  }

  public void setExecuted(String id) {
    if (id != null)
      unavailable.add(id);
    List<HistoricActivityInstance> instances;
    boolean supportsCanceled = Api.feature(HistoricActivityInstanceQuery.class.getName(), "canceled")
        .warn("Outdated Camunda BPM version used will not allow to use " +
            "'" + Scenario.Process.class.getName().replace('$', '.') +
            ".hasCanceled(String activityId)' and '.hasCompleted(String activityId)' methods.");
    if (supportsCanceled) {
      instances = scenarioExecutor.processEngine.getHistoryService()
          .createHistoricActivityInstanceQuery()
          .processInstanceId(processInstance.getId()).canceled().list();
      for (HistoricActivityInstance instance: instances) {
        if (!executed.contains(instance.getId())) {
          if (!started.contains(instance.getId())) {
            scenario.hasStarted(instance.getActivityId());
            started.add(instance.getId());
          }
          scenario.hasFinished(instance.getActivityId());
          scenario.hasCanceled(instance.getActivityId());
          executed.add(instance.getId());
        }
      }
    }
    instances = scenarioExecutor.processEngine.getHistoryService()
        .createHistoricActivityInstanceQuery()
        .processInstanceId(processInstance.getId()).finished().list();
    for (HistoricActivityInstance instance: instances) {
      if (!executed.contains(instance.getId())) {
        if (!started.contains(instance.getId())) {
          scenario.hasStarted(instance.getActivityId());
          started.add(instance.getId());
        }
        scenario.hasFinished(instance.getActivityId());
        if (supportsCanceled) {
          scenario.hasCompleted(instance.getActivityId());
        }
        executed.add(instance.getId());
      }
    }
    instances = scenarioExecutor.processEngine.getHistoryService()
        .createHistoricActivityInstanceQuery()
        .processInstanceId(processInstance.getId()).unfinished().list();
    for (HistoricActivityInstance instance: instances) {
      if (!started.contains(instance.getId())) {
        scenario.hasStarted(instance.getActivityId());
        started.add(instance.getId());
      }
    }
  }

  public Scenario.Process getScenario() {
    return scenario;
  }

}
