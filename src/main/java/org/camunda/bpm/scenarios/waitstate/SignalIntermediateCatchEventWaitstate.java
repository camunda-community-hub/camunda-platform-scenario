package org.camunda.bpm.scenarios.waitstate;


import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.scenarios.Scenario;
import org.camunda.bpm.scenarios.WaitstateAction;
import org.camunda.bpm.scenarios.delegate.EventSubscriptionDelegate;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class SignalIntermediateCatchEventWaitstate extends EventSubscriptionDelegate {

  protected SignalIntermediateCatchEventWaitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine, instance);
  }

  @Override
  protected EventSubscription get() {
    return getRuntimeService().createEventSubscriptionQuery().eventType("signal").executionId(getExecutionId()).singleResult();
  }

  @Override
  protected WaitstateAction action(Scenario scenario) {
    return scenario.atSignalIntermediateCatchEvent(getActivityId());
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
