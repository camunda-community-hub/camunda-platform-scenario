package org.camunda.bpm.scenario.runner;


import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.ScenarioAction;
import org.camunda.bpm.scenario.delegate.EventSubscriptionDelegate;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class SignalIntermediateCatchEventWaitstate extends EventSubscriptionDelegate {

  public SignalIntermediateCatchEventWaitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine, instance);
  }

  @Override
  protected EventSubscription getRuntimeDelegate() {
    return getRuntimeService().createEventSubscriptionQuery().eventType("signal").executionId(getExecutionId()).singleResult();
  }

  @Override
  protected ScenarioAction action(Scenario.Bpmn scenario) {
    return scenario.atSignalIntermediateCatchEvent(getActivityId());
  }

  protected void leave() {
    getRuntimeService().signalEventReceived(getRuntimeDelegate().getEventName(), getRuntimeDelegate().getExecutionId());
  }

  protected void leave(Map<String, Object> variables) {
    getRuntimeService().signalEventReceived(getRuntimeDelegate().getEventName(), getRuntimeDelegate().getExecutionId(), variables);
  }

  public void receiveSignal() {
    leave();
  }

  public void receiveSignal(Map<String, Object> variables) {
    leave(variables);
  }

}
