package org.camunda.bpm.scenario.runner;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricActivityInstanceQuery;
import org.camunda.bpm.engine.impl.persistence.entity.MessageEntity;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstantiationBuilder;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.util.Feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ProcessRunnerImpl implements ProcessRunner, ScenarioRunner<ProcessInstance> {

  protected ScenarioExecutor scenarioExecutor;

  private String processDefinitionKey;
  private ProcessStarter scenarioStarter;
  private Map<String, Object> variables = new HashMap<String, Object>();

  protected Scenario.Process scenario;
  protected ProcessInstance processInstance;

  private Map<String, Boolean> fromActivityIds = new HashMap<String, Boolean>();
  private Map<String, Boolean> toActivityIds = new HashMap<String, Boolean>();
  private Map<String, String> durations = new HashMap<String, String>();

  public ProcessRunnerImpl(ScenarioExecutor scenarioExecutor, Scenario.Process scenario) {
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
  public ProcessRunner fromBefore(String activityId, String... activityIds) {
    setActivityIds(true, true, activityId, activityIds);
    return this;
  }

  @Override
  public ProcessRunner fromAfter(String activityId, String... activityIds) {
    setActivityIds(true, false, activityId, activityIds);
    return this;
  }

  @Override
  public ProcessRunner toBefore(String activityId, String... activityIds) {
    setActivityIds(false, true, activityId, activityIds);
    return this;
  }

  @Override
  public ProcessRunner toAfter(String activityId, String... activityIds) {
    setActivityIds(false, false, activityId, activityIds);
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

  private void setActivityIds(Boolean from, Boolean before, String activityId, String... activityIds) {
    Map<String, Boolean> map = from ? fromActivityIds : toActivityIds;
    map.put(activityId, before);
    for (String a: activityIds) {
      map.put(a, before);
    }
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
  public Waitstate nextWaitstate() {
    continueAsyncContinuations();
    Iterator<Waitstate> it = getNextWaitstates().iterator();
    while (it.hasNext()) {
      Waitstate waitstate = it.next();
      if (isAvailable(waitstate.historicDelegate))
        return waitstate;
    }
    return null;
  }

  private boolean isAvailable(HistoricActivityInstance instance) {
    if (toActivityIds.keySet().contains(instance.getActivityId()) && toActivityIds.get(instance.getActivityId()))
      scenarioExecutor.unavailableHistoricActivityInstances.add(instance.getId());
    return !scenarioExecutor.unavailableHistoricActivityInstances.contains(instance.getId());
  }

  private String getDuration(HistoricActivityInstance instance) {
    if (!durations.containsKey(instance.getId())) {
      durations.put(instance.getId(), scenario.needsTimeUntilFinishing(instance.getActivityId()));
    }
    return durations.get(instance.getId());
  }

  private void continueAsyncContinuations() {
    AsyncContinuation asyncContinuation = nextAsyncContinuation();
    while (asyncContinuation != null) {
      asyncContinuation.leave();
      asyncContinuation = nextAsyncContinuation();
    }
  }

  private AsyncContinuation nextAsyncContinuation() {
    List<Job> jobs = scenarioExecutor.processEngine.getManagementService().createJobQuery().processInstanceId(processInstance.getId()).list();
    for (Job job: jobs) {
      if (job instanceof MessageEntity) {
        MessageEntity entity = (MessageEntity) job;
        if ("async-continuation".equals(entity.getJobHandlerType()))
          return new AsyncContinuation(this, job.getExecutionId());
      }
    }
    return null;
  }

  private List<Waitstate> getNextWaitstates() {
    List<HistoricActivityInstance> instances = scenarioExecutor.processEngine.getHistoryService().createHistoricActivityInstanceQuery().processInstanceId(processInstance.getId()).unfinished().list();
    List<Waitstate> waitstates = new ArrayList<Waitstate>();
    for (HistoricActivityInstance instance: instances) {
      waitstates.add(Waitstate.newInstance(this, instance, getDuration(instance)));
    }
    Collections.sort(waitstates, new Comparator<Waitstate>() {
      @Override
      public int compare(Waitstate one, Waitstate other) {
        return one.getEndTime().compareTo(other.getEndTime());
      }
    });
    return waitstates;
  }

  public void finish() {
    setExecutedHistoricActivityIds(null);
  }

  void setExecutedHistoricActivityIds(HistoricActivityInstance finished) {
    List<HistoricActivityInstance> instances;
    boolean supportsCanceled = Feature.warnIfNotSupported(HistoricActivityInstanceQuery.class.getName(), "canceled");
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
        if (finished != null) {
          if (toActivityIds.keySet().contains(finished.getActivityId())) {
            if (!toActivityIds.get(finished.getActivityId())) {
              scenarioExecutor.unavailableHistoricActivityInstances.add(instance.getId());
            }
          }
        }
        scenario.hasStarted(instance.getActivityId());
        scenarioExecutor.startedHistoricActivityInstances.add(instance.getId());
      }
    }
  }

  @Override
  public Job nextTimerUntil(Waitstate waitstate) {
    List<Job> next = scenarioExecutor.processEngine.getManagementService().createJobQuery().timers().processInstanceId(processInstance.getId()).orderByJobDuedate().asc().listPage(0,1);
    if (!next.isEmpty()) {
      Job timer = next.get(0);
      HistoricActivityInstance intermediateTimer = scenarioExecutor.processEngine.getHistoryService().createHistoricActivityInstanceQuery().unfinished().executionId(timer.getExecutionId()).activityType("intermediateTimer").singleResult();
      HistoricActivityInstance eventBasedGateway = scenarioExecutor.processEngine.getHistoryService().createHistoricActivityInstanceQuery().unfinished().executionId(timer.getExecutionId()).activityType("eventBasedGateway").singleResult();
      if (intermediateTimer == null && eventBasedGateway == null && timer.getDuedate().getTime() <= waitstate.getEndTime().getTime()) {
        return timer;
      }
    }
    return null;
  }

}
