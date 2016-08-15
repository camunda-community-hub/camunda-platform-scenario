package org.camunda.bpm.scenarios;


import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.impl.bpmn.parser.*;
import org.camunda.bpm.engine.runtime.EventSubscription;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class SignalEventWaitstate extends Waitstate<EventSubscription> {

  public SignalEventWaitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine, instance);
  }

  @Override
  protected EventSubscription get() {
    return getRuntimeService().createEventSubscriptionQuery().eventType("signal").executionId(getExecutionId()).singleResult();
  }

  protected static String getActivityType() {
    return "intermediateSignalCatch";
  }

  @Override
  protected void execute(Scenario scenario) {
    scenario.atSignalEvent(getActivityId()).execute(this);
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
