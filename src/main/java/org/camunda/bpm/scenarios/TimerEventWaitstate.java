package org.camunda.bpm.scenarios;


import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.Job;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class TimerEventWaitstate extends Waitstate<Job> {

  protected TimerEventWaitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine, instance);
  }

  @Override
  protected Job get() {
    return getManagementService().createJobQuery().timers().executionId(getExecutionId()).singleResult();
  }

  protected static String getActivityType() {
    return "intermediateTimer";
  }

  @Override
  protected void execute(Scenario scenario) {
    scenario.atTimerEvent(getActivityId()).execute(this);
  }

  protected void leave() {
    getManagementService().executeJob(get().getId());
  }

  protected void leave(Map<String, Object> variables) {
    getRuntimeService().setVariables(getProcessInstance().getId(), variables);
    leave();
  }

  public void triggerTimer() {
    leave();
  }

  public Job getTimer() {
    return get();
  }

}
