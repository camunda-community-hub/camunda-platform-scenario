package org.camunda.bpm.scenario.impl.waitstate;


import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.ScenarioAction;
import org.camunda.bpm.scenario.delegate.EventSubscriptionDelegate;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;
import org.camunda.bpm.scenario.impl.delegate.AbstractEventSubscriptionDelegate;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class SignalIntermediateCatchEventWaitstate extends AbstractEventSubscriptionDelegate {

  public SignalIntermediateCatchEventWaitstate(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner, instance);
  }

  @Override
  protected EventSubscription getDelegate() {
    return getRuntimeService().createEventSubscriptionQuery().eventType("signal").executionId(getExecutionId()).singleResult();
  }

  @Override
  protected ScenarioAction<EventSubscriptionDelegate> action(Scenario.Process scenario) {
    return scenario.actsOnSignalIntermediateCatchEvent(getActivityId());
  }

  protected void leave() {
    getRuntimeService().signalEventReceived(getDelegate().getEventName(), getDelegate().getExecutionId());
  }

  protected void leave(Map<String, Object> variables) {
    getRuntimeService().signalEventReceived(getDelegate().getEventName(), getDelegate().getExecutionId(), variables);
  }

  @Override
  public void receive() {
    leave();
  }

  @Override
  public void receive(Map<String, Object> variables) {
    leave(variables);
  }

}
