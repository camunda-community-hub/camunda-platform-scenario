package org.camunda.bpm.scenarios;


import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.Job;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class TimerEventWaitstate extends Waitstate<Job> {

  public TimerEventWaitstate(ProcessEngine processEngine, String executionId) {
    super(processEngine, executionId);
  }

  @Override
  protected Job get() {
    return getManagementService().createJobQuery().executionId(executionId).singleResult();
  }

  protected void leave() {
    throw new NotImplementedException();
  };

  protected void leave(Map<String, Object> variables) {
    throw new NotImplementedException();
  };

  public void triggerTimer() {
    leave();
  }

  public void triggerTimer(Map<String, Object> variables) {
    leave(variables);
  }

}
