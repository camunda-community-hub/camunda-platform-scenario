package org.camunda.bpm.scenario.runner;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricActivityInstanceQuery;
import org.camunda.bpm.engine.impl.persistence.entity.MessageEntity;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstantiationBuilder;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.ScenarioRunner;
import org.camunda.bpm.scenario.ScenarioStarter;

import java.util.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ScenarioRunnerImpl implements ScenarioRunner {

  private ScenarioStarter scenarioStarter;
  private String processDefinitionKey;

  private Map<String, Boolean> fromActivityIds = new HashMap<String, Boolean>();
  private Map<String, Boolean> toActivityIds = new HashMap<String, Boolean>();
  private Set<String> executedHistoricActivityInstances = new HashSet<String>();
  private Set<String> startedHistoricActivityInstances = new HashSet<String>();
  private Set<String> passedHistoricActivityInstances = new HashSet<String>();
  private Map<String, Object> startVariables = new HashMap<String, Object>();

  private Scenario scenario;
  private ProcessEngine processEngine;
  private ProcessInstance processInstance;

  public static ScenarioRunner run(String processDefinitionKey) {
    return new ScenarioRunnerImpl().running(processDefinitionKey);
  }

  public static ScenarioRunner run(ScenarioStarter scenarioStarter) {
    return new ScenarioRunnerImpl().running(scenarioStarter);
  }

  protected ScenarioRunnerImpl running(ScenarioStarter scenarioStarter) {
    this.scenarioStarter = scenarioStarter;
    return this;
  }

  protected ScenarioRunnerImpl running(String processDefinitionKey) {
    this.processDefinitionKey = processDefinitionKey;
    return this;
  }

  protected ScenarioRunnerImpl running(ProcessInstance processInstance) {
    this.processInstance = processInstance;
    return this;
  }

  @Override
  public ScenarioRunnerImpl variables(Map<String, Object> variables) {
    this.startVariables = variables;
    return this;
  }

  @Override
  public ScenarioRunnerImpl fromBefore(String activityId, String... activityIds) {
    put(true, true, activityId, activityIds);
    return this;
  }

  @Override
  public ScenarioRunnerImpl fromAfter(String activityId, String... activityIds) {
    put(true, false, activityId, activityIds);
    return this;
  }

  @Override
  public ScenarioRunnerImpl toBefore(String activityId, String... activityIds) {
    put(false, true, activityId, activityIds);
    return this;
  }

  @Override
  public ScenarioRunnerImpl toAfter(String activityId, String... activityIds) {
    put(false, false, activityId, activityIds);
    return this;
  }

  private void put(Boolean from, Boolean before, String activityId, String... activityIds) {
    Map<String, Boolean> map = from ? fromActivityIds : toActivityIds;
    map.put(activityId, before);
    for (String a: activityIds) {
      map.put(a, before);
    }
  }

  private void init(Scenario scenario, ProcessEngine processEngine) {
    this.scenario = scenario;
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
    if (this.processInstance == null && this.scenarioStarter == null) {
      this.scenarioStarter = new ScenarioStarter() {
        @Override
        public ProcessInstance start() {
          ProcessInstantiationBuilder builder = ScenarioRunnerImpl.this.processEngine.getRuntimeService().createProcessInstanceByKey(processDefinitionKey);
          for (String activityId: fromActivityIds.keySet()) {
            Boolean from = fromActivityIds.get(activityId);
            if (from) {
              builder.startBeforeActivity(activityId);
            } else {
              builder.startAfterActivity(activityId);
            }
          }
          if (startVariables != null) {
            builder.setVariables(startVariables);
          }
          return builder.executeWithVariablesInReturn();
        }
      };
    }
  }

  @Override
  public ProcessInstance run(Scenario scenario) {
    return run(scenario, null);
  }

  @Override
  public ProcessInstance run(Scenario scenario, ProcessEngine processEngine) {
    init(scenario, processEngine);
    if (processInstance == null)
      processInstance = scenarioStarter.start();
    for (boolean lastCall: new boolean[] { false, true }) {
      Waitstate waitstate = nextWaitstate(lastCall);
      while (waitstate != null) {
        setExecutedHistoricActivityIds();
        waitstate.execute(scenario);
        if (waitstate.unfinished())
          executedHistoricActivityInstances.add(waitstate.historicDelegate.getId());
        waitstate = nextWaitstate(lastCall);
      }
    }
    setExecutedHistoricActivityIds();
    return processInstance;
  }

  private Waitstate nextWaitstate(boolean lastCall) {
    continueAsyncContinuations();
    List<HistoricActivityInstance> instances = createWaitstateQuery().list();
    for (HistoricActivityInstance instance: instances) {
      if (instance.getActivityType().equals("intermediateTimer"))
        continue;
      if (isAvailable(instance, lastCall))
        return Waitstate.newInstance(processEngine, instance);
    }
    return nextTimerEventWaitstate(lastCall);
  }

  private Waitstate nextTimerEventWaitstate(boolean lastCall) {
    List<HistoricActivityInstance> instances = createWaitstateQuery().activityType("intermediateTimer").list();
    if (!instances.isEmpty()) {
      List<Job> timers = processEngine.getManagementService().createJobQuery().timers().orderByJobDuedate().asc().list();
      for (Job timer: timers) {
        for (HistoricActivityInstance instance: instances) {
          if (instance.getExecutionId().equals(timer.getExecutionId()))
            if (isAvailable(instance, lastCall))
              return Waitstate.newInstance(processEngine, instance);
        }
      }
    }
    return null;
  }

  private AsyncContinuation nextAsyncContinuation() {
    List<Job> jobs = processEngine.getManagementService().createJobQuery().list();
    for (Job job: jobs) {
      if (job instanceof MessageEntity) {
        MessageEntity entity = (MessageEntity) job;
        if ("async-continuation".equals(entity.getJobHandlerType()))
          return new AsyncContinuation(processEngine, job.getExecutionId());
      }
    }
    return null;
  }

  private void continueAsyncContinuations() {
    AsyncContinuation asyncContinuation = nextAsyncContinuation();
    while (asyncContinuation != null) {
      asyncContinuation.leave();
      asyncContinuation = nextAsyncContinuation();
    }
  }

  private HistoricActivityInstanceQuery createWaitstateQuery() {
    return processEngine.getHistoryService().createHistoricActivityInstanceQuery().processInstanceId(processInstance.getId()).unfinished();
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

  private void setExecutedHistoricActivityIds() {
    List<HistoricActivityInstance> instances = processEngine.getHistoryService()
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
        scenario.hasCompleted(instance.getActivityId());
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

}
