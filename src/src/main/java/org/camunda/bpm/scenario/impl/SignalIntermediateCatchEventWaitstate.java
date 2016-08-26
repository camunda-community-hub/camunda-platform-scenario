package org.camunda.bpm.scenario.impl;


import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.ScenarioAction;
import org.camunda.bpm.scenario.delegate.SignalEventSubscriptionDelegate;
import org.camunda.bpm.scenario.impl.delegate.AbstractSignalEventSubscriptionDelegate;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class SignalIntermediateCatchEventWaitstate extends AbstractSignalEventSubscriptionDelegate {

  public SignalIntermediateCatchEventWaitstate(ProcessRunnerImpl runner, HistoricActivityInstance instance, String duration) {
    super(runner, instance, duration);
  }

  @Override
  protected EventSubscription getRuntimeDelegate() {
    return getRuntimeService().createEventSubscriptionQuery().eventType("signal").executionId(getExecutionId()).singleResult();
  }

  @Override
  protected ScenarioAction<SignalEventSubscriptionDelegate> action(Scenario.Process scenario) {
    return scenario.actsOnSignalIntermediateCatchEvent(getActivityId());
  }

  protected void leave() {
    getRuntimeService().signalEventReceived(getRuntimeDelegate().getEventName(), getRuntimeDelegate().getExecutionId());
  }

  protected void leave(Map<String, Object> variables) {
    getRuntimeService().signalEventReceived(getRuntimeDelegate().getEventName(), getRuntimeDelegate().getExecutionId(), variables);
  }

  @Override
  public void receiveSignal() {
    leave();
  }

  @Override
  public void receiveSignal(Map<String, Object> variables) {
    leave(variables);
  }

}
