package org.camunda.bpm.scenario.runner;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricActivityInstanceQuery;
import org.camunda.bpm.engine.impl.persistence.entity.MessageEntity;
import org.camunda.bpm.engine.impl.util.ClockUtil;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ScenarioRunnerImpl implements ProcessRunner {

  protected ProcessEngine processEngine;

  private String processDefinitionKey;
  private ProcessStarter scenarioStarter;
  private Map<String, Object> variables = new HashMap<String, Object>();

  private Scenario.Process scenario;
  private ProcessInstance processInstance;

  private List<ScenarioRunnerImpl> runners = new ArrayList<ScenarioRunnerImpl>();

  private Map<String, Boolean> fromActivityIds = new HashMap<String, Boolean>();
  private Map<String, Boolean> toActivityIds = new HashMap<String, Boolean>();
  private Set<String> executedHistoricActivityInstances = new HashSet<String>();
  private Set<String> startedHistoricActivityInstances = new HashSet<String>();
  private Set<String> passedHistoricActivityInstances = new HashSet<String>();
  private Map<String, String> durations = new HashMap<String, String>();

  public ScenarioRunnerImpl(Scenario.Process scenario) {
    this.scenario = scenario;
    this.runners.add(this);
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
  public ScenarioRunnerImpl fromBefore(String activityId, String... activityIds) {
    setActivityIds(true, true, activityId, activityIds);
    return this;
  }

  @Override
  public ScenarioRunnerImpl fromAfter(String activityId, String... activityIds) {
    setActivityIds(true, false, activityId, activityIds);
    return this;
  }

  @Override
  public ScenarioRunnerImpl toBefore(String activityId, String... activityIds) {
    setActivityIds(false, true, activityId, activityIds);
    return this;
  }

  @Override
  public ScenarioRunnerImpl toAfter(String activityId, String... activityIds) {
    setActivityIds(false, false, activityId, activityIds);
    return this;
  }

  @Override
  public ProcessRunner engine(ProcessEngine processEngine) {
    if (this.processEngine == null || processEngine != null) {
      if (processEngine == null) {
        Map<String, ProcessEngine> processEngines = ProcessEngines.getProcessEngines();
        if (processEngines.size() == 1) {
          this.processEngine = processEngines.values().iterator().next();
        } else {
          String message = processEngines.size() == 0 ? "No ProcessEngine found to be " +
              "registered with " + ProcessEngines.class.getSimpleName() + "!"
              : String.format(processEngines.size() + " ProcessEngines initialized. " +
              "Explicitely initialise engine by calling " + ScenarioRunnerImpl.class.getSimpleName() +
              "(scenario, engine)");
          throw new IllegalStateException(message);
        }
      } else {
        this.processEngine = processEngine;
      }
    }
    return this;
  }

  @Override
  public ProcessInstance execute() {
    init(scenario);
    if (processInstance == null)
      processInstance = scenarioStarter.start();
    for (boolean lastCall: new boolean[] { false, true }) {
      Waitstate waitstate = nextWaitstate(lastCall);
      while (waitstate != null) {
        setExecutedHistoricActivityIds();
        boolean executable = fastForward(waitstate);
        if (executable) {
          waitstate.execute(scenario);
          executedHistoricActivityInstances.add(waitstate.historicDelegate.getId());
        }
        waitstate = nextWaitstate(lastCall);
      }
    }
    setExecutedHistoricActivityIds();
    return processInstance;
  }

  private void continueAsyncContinuations() {
    AsyncContinuation asyncContinuation = nextAsyncContinuation();
    while (asyncContinuation != null) {
      asyncContinuation.leave();
      asyncContinuation = nextAsyncContinuation();
    }
  }

  private AsyncContinuation nextAsyncContinuation() {
    List<Job> jobs = processEngine.getManagementService().createJobQuery().processInstanceId(processInstance.getId()).list();
    for (Job job: jobs) {
      if (job instanceof MessageEntity) {
        MessageEntity entity = (MessageEntity) job;
        if ("async-continuation".equals(entity.getJobHandlerType()))
          return new AsyncContinuation(processEngine, job.getExecutionId());
      }
    }
    return null;
  }

  protected boolean fastForward(Waitstate waitstate) {
    Date endTime = waitstate.getEndTime();
    // TODO determine next timer for all runners
    List<Job> next = processEngine.getManagementService().createJobQuery().timers().orderByJobDuedate().asc().listPage(0,1);
    if (!next.isEmpty()) {
      Job timer = next.get(0);
      HistoricActivityInstance intermediateTimer = processEngine.getHistoryService().createHistoricActivityInstanceQuery().unfinished().executionId(timer.getExecutionId()).activityType("intermediateTimer").singleResult();
      if (intermediateTimer == null && timer.getDuedate().getTime() <= endTime.getTime()) {
        ClockUtil.setCurrentTime(new Date(timer.getDuedate().getTime() + 1));
        processEngine.getManagementService().executeJob(timer.getId());
        ClockUtil.setCurrentTime(new Date(timer.getDuedate().getTime()));
        return false;
      }
    }
    ClockUtil.setCurrentTime(endTime);
    return true;
  }

  private Waitstate nextWaitstate(boolean lastCall) {
    continueAsyncContinuations();
    // TODO determine next waitstate for all runners
    Iterator<Waitstate> it = getNextWaitstates().iterator();
    while (it.hasNext()) {
      Waitstate waitstate = it.next();
      if (isAvailable(waitstate.historicDelegate, lastCall))
        return waitstate;
    }
    return null;
  }

  private HistoricActivityInstanceQuery createWaitstateQuery() {
    return processEngine.getHistoryService().createHistoricActivityInstanceQuery().processInstanceId(processInstance.getId()).unfinished();
  }

  private List<Waitstate> getNextWaitstates() {
    List<HistoricActivityInstance> instances = createWaitstateQuery().list();
    List<Waitstate> waitstates = new ArrayList<Waitstate>();
    for (HistoricActivityInstance instance: instances) {
      waitstates.add(Waitstate.newInstance(processEngine, instance, getDuration(instance)));
    }
    Collections.sort(waitstates, new Comparator<Waitstate>() {
      @Override
      public int compare(Waitstate one, Waitstate other) {
        return one.getEndTime().compareTo(other.getEndTime());
      }
    });
    return waitstates;
  }

  private boolean isAvailable(HistoricActivityInstance instance, boolean lastCall) {
    if (lastCall) {
      for (String activityId: toActivityIds.keySet()) {
        if (!toActivityIds.get(activityId))
          if (activityId.equals(instance.getActivityId()))
            return true;
      }
    } else {
      if (!toActivityIds.keySet().contains(instance.getActivityId()))
        if (!executedHistoricActivityInstances.contains(instance.getId()))
          return true;
    }
    return false;
  }

  private String getDuration(HistoricActivityInstance instance) {
    if (!durations.containsKey(instance.getId())) {
      durations.put(instance.getId(), scenario.needsTimeUntilFinishing(instance.getActivityId()));
    }
    return durations.get(instance.getId());
  }

  private void setExecutedHistoricActivityIds() {
    List<HistoricActivityInstance> instances;
    boolean supportsCanceled = Feature.warnIfNotSupported(HistoricActivityInstanceQuery.class.getName(), "canceled");
    // TODO determine executed historic activity ids for all runners
    if (supportsCanceled) {
      instances = processEngine.getHistoryService()
          .createHistoricActivityInstanceQuery()
          .processInstanceId(processInstance.getId()).canceled().list();
      for (HistoricActivityInstance instance: instances) {
        if (!passedHistoricActivityInstances.contains(instance.getId())) {
          if (!startedHistoricActivityInstances.contains(instance.getId())) {
            scenario.hasStarted(instance.getActivityId());
            startedHistoricActivityInstances.add(instance.getId());
          }
          scenario.hasFinished(instance.getActivityId());
          scenario.hasCanceled(instance.getActivityId());
          passedHistoricActivityInstances.add(instance.getId());
        }
      }
    }
    instances = processEngine.getHistoryService()
        .createHistoricActivityInstanceQuery()
        .processInstanceId(processInstance.getId()).finished().list();
    for (HistoricActivityInstance instance: instances) {
      if (!passedHistoricActivityInstances.contains(instance.getId())) {
        if (!startedHistoricActivityInstances.contains(instance.getId())) {
          scenario.hasStarted(instance.getActivityId());
          startedHistoricActivityInstances.add(instance.getId());
        }
        scenario.hasFinished(instance.getActivityId());
        if (supportsCanceled) {
          scenario.hasCompleted(instance.getActivityId());
        }
        passedHistoricActivityInstances.add(instance.getId());
      }
    }
    instances = processEngine.getHistoryService()
        .createHistoricActivityInstanceQuery()
        .processInstanceId(processInstance.getId()).unfinished().list();
    for (HistoricActivityInstance instance: instances) {
      if (!startedHistoricActivityInstances.contains(instance.getId())) {
        scenario.hasStarted(instance.getActivityId());
        startedHistoricActivityInstances.add(instance.getId());
      }
    }
  }

  protected ScenarioRunnerImpl running(ProcessInstance processInstance) {
    this.processInstance = processInstance;
    return this;
  }

  private void setActivityIds(Boolean from, Boolean before, String activityId, String... activityIds) {
    Map<String, Boolean> map = from ? fromActivityIds : toActivityIds;
    map.put(activityId, before);
    for (String a: activityIds) {
      map.put(a, before);
    }
  }

  private void init(Scenario.Process scenario) {
    engine(null);
    this.scenario = scenario;
    if (this.processInstance == null && this.scenarioStarter == null) {
      this.scenarioStarter = new ProcessStarter() {
        @Override
        public ProcessInstance start() {
          if (fromActivityIds.isEmpty()) {
            return ScenarioRunnerImpl.this.processEngine.getRuntimeService().startProcessInstanceByKey(processDefinitionKey, variables);
          } else {
            ProcessInstantiationBuilder builder = ScenarioRunnerImpl.this.processEngine.getRuntimeService().createProcessInstanceByKey(processDefinitionKey);
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
    ClockUtil.setCurrentTime(new Date());
  }

}
