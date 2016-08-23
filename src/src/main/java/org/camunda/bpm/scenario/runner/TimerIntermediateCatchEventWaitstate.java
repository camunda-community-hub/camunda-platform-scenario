package org.camunda.bpm.scenario.runner;


import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.ScenarioAction;
import org.camunda.bpm.scenario.delegate.JobDelegate;

import java.util.Date;
import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class TimerIntermediateCatchEventWaitstate extends JobDelegate {

  public TimerIntermediateCatchEventWaitstate(ProcessEngine processEngine, HistoricActivityInstance instance, String duration) {
    super(processEngine, instance, duration);
  }

  @Override
  protected Job getRuntimeDelegate() {
    return getManagementService().createJobQuery().timers().executionId(getExecutionId()).singleResult();
  }

  @Override
  protected ScenarioAction action(Scenario.Process scenario) {
    return scenario.atTimerIntermediateCatchEvent(getActivityId());
  }

  protected void leave() {
    getManagementService().executeJob(getRuntimeDelegate().getId());
  }

  protected void leave(Map<String, Object> variables) {
    getRuntimeService().setVariables(getProcessInstance().getId(), variables);
    leave();
  }

  @Override
  protected boolean isSelf(Job timer) {
    return !timer.getId().equals(getId());
  }

  public void trigger() {
    leave();
  }

  @Override
  protected Date getEndTime() {
    return getDuedate();
  }

}
