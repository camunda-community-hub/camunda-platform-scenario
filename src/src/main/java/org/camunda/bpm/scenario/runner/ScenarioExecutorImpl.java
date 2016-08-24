package org.camunda.bpm.scenario.runner;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricActivityInstanceQuery;
import org.camunda.bpm.engine.impl.persistence.entity.MessageEntity;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.util.Feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ScenarioExecutorImpl implements ProcessRunner {

  protected ProcessEngine processEngine;

  List<ScenarioRunnerImpl> runners = new ArrayList<ScenarioRunnerImpl>();

  Set<String> executedHistoricActivityInstances = new HashSet<String>();
  Set<String> startedHistoricActivityInstances = new HashSet<String>();
  Set<String> passedHistoricActivityInstances = new HashSet<String>();

  public ScenarioExecutorImpl(Scenario.Process scenario) {
    this.runners.add(new ScenarioRunnerImpl(this, scenario));
  }

  @Override
  public ProcessRunner startBy(String processDefinitionKey) {
    return runners.get(runners.size() - 1).startBy(processDefinitionKey);
  }

  @Override
  public ProcessRunner startBy(String processDefinitionKey, Map<String, Object> variables) {
    return runners.get(runners.size() - 1).startBy(processDefinitionKey, variables);
  }

  @Override
  public ProcessRunner startBy(ProcessStarter starter) {
    return runners.get(runners.size() - 1).startBy(starter);
  }

  @Override
  public ProcessRunner toBefore(String activityId, String... activityIds) {
    return runners.get(runners.size() - 1).toBefore(activityId, activityIds);
  }

  @Override
  public ProcessRunner toAfter(String activityId, String... activityIds) {
    return runners.get(runners.size() - 1).toAfter(activityId, activityIds);
  }

  @Override
  public ProcessRunner fromBefore(String activityId, String... activityIds) {
    return runners.get(runners.size() - 1).fromBefore(activityId, activityIds);
  }

  @Override
  public ProcessRunner fromAfter(String activityId, String... activityIds) {
    return runners.get(runners.size() - 1).fromAfter(activityId, activityIds);
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
              "Explicitely initialise engine by calling " + ScenarioExecutorImpl.class.getSimpleName() +
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
    engine(null);
    ClockUtil.reset();
    ProcessInstance processInstance = null;
    for (ScenarioRunnerImpl runner: runners) {
      processInstance = runner.run(); // TODO delivers last started process instance for now...
    }
    for (boolean lastCall: new boolean[] { false, true }) {
      Waitstate waitstate = nextWaitstate(lastCall);
      while (waitstate != null) {
        boolean executable = fastForward(waitstate);
        if (executable) {
          waitstate.execute();
          executedHistoricActivityInstances.add(waitstate.historicDelegate.getId());
        }
        waitstate = nextWaitstate(lastCall);
      }
    }
    return processInstance;
  }

  protected Waitstate nextWaitstate(boolean lastCall) {
    List<Waitstate> waitstates = new ArrayList<Waitstate>();
    for (ScenarioRunnerImpl runner: runners) {
      Waitstate waitstate = runner.nextWaitstate(lastCall);
      if (waitstate != null)
        waitstates.add(waitstate);
    }
    if (!waitstates.isEmpty()) {
      Collections.sort(waitstates, new Comparator<Waitstate>() {
        @Override
        public int compare(Waitstate one, Waitstate other) {
          return one.getEndTime().compareTo(other.getEndTime());
        }
      });
      return waitstates.get(0);
    }
    return null;
  }

  protected boolean fastForward(Waitstate waitstate) {
    Date endTime = waitstate.getEndTime();
    List<Job> timers = new ArrayList<Job>();
    for (ScenarioRunnerImpl runner: runners) {
      Job timer = runner.nextTimerUntil(endTime);
      if (timer != null)
        timers.add(timer);
    }
    if (!timers.isEmpty()) {
      Collections.sort(timers, new Comparator<Job>() {
        @Override
        public int compare(Job one, Job other) {
          return one.getDuedate().compareTo(other.getDuedate());
        }
      });
      Job timer = timers.get(0);
      ClockUtil.setCurrentTime(new Date(timer.getDuedate().getTime() + 1));
      processEngine.getManagementService().executeJob(timer.getId());
      ClockUtil.setCurrentTime(new Date(timer.getDuedate().getTime()));
      return false;
    }
    ClockUtil.setCurrentTime(endTime);
    return true;
  }

}
