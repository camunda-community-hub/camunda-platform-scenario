package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.Scenario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ScenarioExecutorImpl {

  protected ProcessEngine processEngine;

  public List<Runner> runners = new ArrayList<Runner>();

  Set<String> unavailableHistoricActivityInstances = new HashSet<String>();
  Set<String> startedHistoricActivityInstances = new HashSet<String>();
  Set<String> passedHistoricActivityInstances = new HashSet<String>();

  public ScenarioExecutorImpl(Scenario.Process scenario) {
    this.runners.add(new ProcessRunnerImpl(this, scenario));
  }

  public void engine(ProcessEngine processEngine) {
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
  }

  protected ProcessInstance execute() {
    engine(null);
    ClockUtil.reset();
    ProcessInstance processInstance = null;
    for (Runner runner: runners) {
      processInstance = (ProcessInstance) runner.run(); // TODO delivers last started process instance for now...
    }
    ExecutableWaitstate waitstate = nextWaitstate();
    while (waitstate != null) {
      boolean executable = fastForward(waitstate);
      if (executable) {
        waitstate.execute();
        unavailableHistoricActivityInstances.add(waitstate.historicDelegate.getId());
      }
      waitstate = nextWaitstate();
    }
    for (Runner runner: runners) {
      runner.finish();
    }
    return processInstance;
  }

  protected ExecutableWaitstate nextWaitstate() {
    List<ExecutableWaitstate> waitstates = new ArrayList<ExecutableWaitstate>();
    for (Runner runner: runners) {
      ExecutableWaitstate waitstate = (ExecutableWaitstate) runner.next();
      if (waitstate != null)
        waitstates.add(waitstate);
    }
    if (!waitstates.isEmpty()) {
      Collections.sort(waitstates);
      return waitstates.get(0);
    }
    return null;
  }

  protected boolean fastForward(ExecutableWaitstate waitstate) {
    Date endTime = waitstate.isExecutableAt();
    List<ExecutableJob> timers = new ArrayList<ExecutableJob>();
    for (Runner runner: runners) {
      Job timer = runner.next(waitstate);
      if (timer != null)
        timers.add(Executable.Jobs.newInstance((ProcessRunnerImpl) runner, timer));
    }
    if (!timers.isEmpty()) {
      Collections.sort(timers);
      ExecutableJob timer = timers.get(0);
      timer.execute();
      return false;
    }
    ClockUtil.setCurrentTime(endTime);
    return true;
  }

}
