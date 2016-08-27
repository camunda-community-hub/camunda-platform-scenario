package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricActivityInstanceQuery;
import org.camunda.bpm.engine.impl.persistence.entity.MessageEntity;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstantiationBuilder;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.runner.ProcessRunner;
import org.camunda.bpm.scenario.runner.ProcessStarter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ProcessRunnerImpl implements ProcessRunner, ScenarioRunner<ProcessInstance> {

  protected ScenarioExecutorImpl scenarioExecutor;

  private String processDefinitionKey;
  private ProcessStarter scenarioStarter;
  private Map<String, Object> variables = new HashMap<String, Object>();

  protected Scenario.Process scenario;
  protected ProcessInstance processInstance;

  private Map<String, Boolean> fromActivityIds = new HashMap<String, Boolean>();
  private Map<String, String> durations = new HashMap<String, String>();

  public ProcessRunnerImpl(ScenarioExecutorImpl scenarioExecutor, Scenario.Process scenario) {
    this.scenarioExecutor = scenarioExecutor;
    this.scenario = scenario;
  }

  @Override
  public ProcessRunner startBy(ProcessStarter scenarioStarter) {
    this.scenarioStarter = scenarioStarter;
    return this;
  }

  @Override
  public ProcessRunner startBy(String processDefinitionKey) {
    this.processDefinitionKey = processDefinitionKey;
    return this;
  }

  @Override
  public ProcessRunner startBy(String processDefinitionKey, Map<String, Object> variables) {
    this.processDefinitionKey = processDefinitionKey;
    this.variables = variables;
    return this;
  }

  @Override
  public ProcessRunner fromBefore(String activityId) {
    Api.feature(RuntimeService.class.getName(), "createProcessInstanceByKey", String.class)
        .fail("Outdated Camunda BPM version used will not allow to start process instances " +
            "at explicitely selected activity IDs");
    fromActivityIds.put(activityId, true);
    return this;
  }

  @Override
  public ProcessRunner fromAfter(String activityId) {
    Api.feature(RuntimeService.class.getName(), "createProcessInstanceByKey", String.class)
        .fail("Outdated Camunda BPM version used will not allow to start process instances " +
            "at explicitely selected activity IDs");
    fromActivityIds.put(activityId, false);
    return this;
  }

  @Override
  public ProcessRunner engine(ProcessEngine processEngine) {
    scenarioExecutor.engine(processEngine);
    return this;
  }

  @Override
  public ProcessInstance execute() {
    return scenarioExecutor.execute();
  }

  protected void running(CallActivityWaitstate waitstate) {
    this.scenarioExecutor = waitstate.runner.scenarioExecutor;
    this.scenarioExecutor.runners.add(this);
    this.processInstance = waitstate;
    setExecutedHistoricActivityIds(null);
  }

  @Override
  public ProcessInstance run() {
    if (this.processInstance == null && this.scenarioStarter == null) {
      this.scenarioStarter = new ProcessStarter() {
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
    }
    this.processInstance = scenarioStarter.start();
    setExecutedHistoricActivityIds(null);
    return this.processInstance;
  }

  @Override
  public ExecutableWaitstate next() {
    continueAsyncContinuations();
    Iterator<ExecutableWaitstate> it = getNextWaitstates().iterator();
    while (it.hasNext()) {
      ExecutableWaitstate waitstate = it.next();
      if (isAvailable(waitstate.historicDelegate))
        return waitstate;
    }
    return null;
  }

  private boolean isAvailable(HistoricActivityInstance instance) {
    return !scenarioExecutor.unavailableHistoricActivityInstances.contains(instance.getId());
  }

  private String getDuration(HistoricActivityInstance instance) {
    if (!durations.containsKey(instance.getId())) {
      durations.put(instance.getId(), scenario.waitsForActionOn(instance.getActivityId()));
    }
    return durations.get(instance.getId());
  }

  private void continueAsyncContinuations() {
    ExecutableJob executableJob = nextAsyncContinuation();
    while (executableJob != null) {
      executableJob.leave();
      executableJob = nextAsyncContinuation();
    }
  }

  private ExecutableJob nextAsyncContinuation() {
    List<Job> jobs = scenarioExecutor.processEngine.getManagementService().createJobQuery().processInstanceId(processInstance.getId()).list();
    for (Job job: jobs) {
      if (job instanceof MessageEntity) {
        MessageEntity entity = (MessageEntity) job;
        if ("async-continuation".equals(entity.getJobHandlerType()))
          return new ExecutableJob(this, job);
      }
    }
    return null;
  }

  private List<ExecutableWaitstate> getNextWaitstates() {
    List<HistoricActivityInstance> instances = scenarioExecutor.processEngine.getHistoryService().createHistoricActivityInstanceQuery().processInstanceId(processInstance.getId()).unfinished().list();
    List<ExecutableWaitstate> waitstates = new ArrayList<ExecutableWaitstate>();
    for (HistoricActivityInstance instance: instances) {
      waitstates.add(ExecutableWaitstate.newInstance(this, instance, getDuration(instance)));
    }
    Collections.sort(waitstates, new Comparator<ExecutableWaitstate>() {
      @Override
      public int compare(ExecutableWaitstate one, ExecutableWaitstate other) {
        return one.isExecutableAt().compareTo(other.isExecutableAt());
      }
    });
    return waitstates;
  }

  public void finish() {
    setExecutedHistoricActivityIds(null);
  }

  void setExecutedHistoricActivityIds(HistoricActivityInstance finished) {
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
        if (!scenarioExecutor.passedHistoricActivityInstances.contains(instance.getId())) {
          if (!scenarioExecutor.startedHistoricActivityInstances.contains(instance.getId())) {
            scenario.hasStarted(instance.getActivityId());
            scenarioExecutor.startedHistoricActivityInstances.add(instance.getId());
          }
          scenario.hasFinished(instance.getActivityId());
          scenario.hasCanceled(instance.getActivityId());
          scenarioExecutor.passedHistoricActivityInstances.add(instance.getId());
        }
      }
    }
    instances = scenarioExecutor.processEngine.getHistoryService()
        .createHistoricActivityInstanceQuery()
        .processInstanceId(processInstance.getId()).finished().list();
    for (HistoricActivityInstance instance: instances) {
      if (!scenarioExecutor.passedHistoricActivityInstances.contains(instance.getId())) {
        if (!scenarioExecutor.startedHistoricActivityInstances.contains(instance.getId())) {
          scenario.hasStarted(instance.getActivityId());
          scenarioExecutor.startedHistoricActivityInstances.add(instance.getId());
        }
        scenario.hasFinished(instance.getActivityId());
        if (supportsCanceled) {
          scenario.hasCompleted(instance.getActivityId());
        }
        scenarioExecutor.passedHistoricActivityInstances.add(instance.getId());
      }
    }
    instances = scenarioExecutor.processEngine.getHistoryService()
        .createHistoricActivityInstanceQuery()
        .processInstanceId(processInstance.getId()).unfinished().list();
    for (HistoricActivityInstance instance: instances) {
      if (!scenarioExecutor.startedHistoricActivityInstances.contains(instance.getId())) {
        scenario.hasStarted(instance.getActivityId());
        scenarioExecutor.startedHistoricActivityInstances.add(instance.getId());
      }
    }
  }

  @Override
  public Job next(ExecutableWaitstate waitstate) {
    List<Job> next = scenarioExecutor.processEngine.getManagementService().createJobQuery().timers().processInstanceId(processInstance.getId()).orderByJobDuedate().asc().listPage(0,1);
    if (!next.isEmpty()) {
      Job timer = next.get(0);
      HistoricActivityInstance intermediateTimer = scenarioExecutor.processEngine.getHistoryService().createHistoricActivityInstanceQuery().unfinished().executionId(timer.getExecutionId()).activityType("intermediateTimer").singleResult();
      HistoricActivityInstance eventBasedGateway = scenarioExecutor.processEngine.getHistoryService().createHistoricActivityInstanceQuery().unfinished().executionId(timer.getExecutionId()).activityType("eventBasedGateway").singleResult();
      if (intermediateTimer == null && eventBasedGateway == null && timer.getDuedate().getTime() <= waitstate.isExecutableAt().getTime()) {
        return timer;
      }
    }
    return null;
  }

}
