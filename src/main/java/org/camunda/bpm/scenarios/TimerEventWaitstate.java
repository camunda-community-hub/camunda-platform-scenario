package org.camunda.bpm.scenarios;


import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.Job;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class TimerEventWaitstate extends Waitstate<Job> {

  public TimerEventWaitstate(ProcessEngine processEngine, String executionId, String activityId) {
    super(processEngine, executionId, activityId);
  }

  @Override
  protected Job get() {
    return getManagementService().createJobQuery().timers().executionId(executionId).singleResult();
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
