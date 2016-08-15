package org.camunda.bpm.scenarios;


import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.EventSubscription;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class MessageEventWaitstate extends Waitstate<EventSubscription> {

  public MessageEventWaitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine, instance);
  }

  @Override
  protected EventSubscription get() {
    return getRuntimeService().createEventSubscriptionQuery().eventType("message").executionId(getExecutionId()).singleResult();
  }

  protected static String getActivityType() {
    return "intermediateMessageCatch";
  }

  @Override
  protected void execute(Scenario scenario) {
    scenario.atMessageEvent(getActivityId()).execute(this);
  }

  protected void leave() {
    getRuntimeService().signalEventReceived(get().getEventName(), get().getExecutionId());
  }

  protected void leave(Map<String, Object> variables) {
    getRuntimeService().signalEventReceived(get().getEventName(), get().getExecutionId(), variables);
  }

  public void receiveMessage() {
    leave();
  }

  public void receiveMessage(Map<String, Object> variables) {
    leave(variables);
  }

  public EventSubscription getEventSubscription() {
    return get();
  }

}
