package org.camunda.bpm.scenarios.runner;


import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.scenarios.Scenario;
import org.camunda.bpm.scenarios.ScenarioAction;
import org.camunda.bpm.scenarios.delegate.JobDelegate;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class TimerIntermediateCatchEventWaitstate extends JobDelegate {

  protected TimerIntermediateCatchEventWaitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine, instance);
  }

  @Override
  protected Job get() {
    return getManagementService().createJobQuery().timers().executionId(getExecutionId()).singleResult();
  }

  @Override
  protected ScenarioAction action(Scenario scenario) {
    return scenario.atTimerIntermediateCatchEvent(getActivityId());
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

}
