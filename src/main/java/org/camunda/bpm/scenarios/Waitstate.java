package org.camunda.bpm.scenarios;

import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.runtime.*;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class Waitstate<O> extends Savepoint<O> {

  protected String activityId;

  protected Waitstate(ProcessEngine processEngine, String executionId, String activityId) {
    super(processEngine, executionId);
    this.activityId = activityId;
  }

  protected abstract void leave();

  protected abstract void leave(Map<String, Object> variables);

  public SignalEventReceivedBuilder createSignal(String signalName) {
    return getRuntimeService().createSignalEvent(signalName);
  }

  public MessageCorrelationBuilder createMessage(String messageName) {
    return getRuntimeService().createMessageCorrelation(messageName);
  }

  public void triggerTimer(String activityId) {
    Job job = getManagementService().createJobQuery().processInstanceId(getProcessInstance().getId()).activityId(activityId).timers().singleResult();
    getManagementService().executeJob(job.getId());
  }

}
