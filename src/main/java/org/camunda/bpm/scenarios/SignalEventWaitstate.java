package org.camunda.bpm.scenarios;


import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.EventSubscription;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class SignalEventWaitstate extends Waitstate<EventSubscription> {

  public SignalEventWaitstate(ProcessEngine processEngine, String executionId, String activityId) {
    super(processEngine, executionId, activityId);
  }

  @Override
  protected EventSubscription get() {
    return getRuntimeService().createEventSubscriptionQuery().eventType("signal").executionId(executionId).singleResult();
  }

  protected void leave() {
    getRuntimeService().signalEventReceived(get().getEventName(), get().getExecutionId());
  }

  protected void leave(Map<String, Object> variables) {
    getRuntimeService().signalEventReceived(get().getEventName(), get().getExecutionId(), variables);
  }

  public void receiveSignal() {
    leave();
  }

  public void receiveSignal(Map<String, Object> variables) {
    leave(variables);
  }

}
