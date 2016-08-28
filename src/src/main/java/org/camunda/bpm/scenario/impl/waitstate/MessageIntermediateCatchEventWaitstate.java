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
public class MessageIntermediateCatchEventWaitstate extends AbstractEventSubscriptionDelegate {

  public MessageIntermediateCatchEventWaitstate(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner, instance);
  }

  @Override
  protected EventSubscription getDelegate() {
    return getRuntimeService().createEventSubscriptionQuery().eventType("message").executionId(getExecutionId()).singleResult();
  }

  @Override
  protected ScenarioAction<EventSubscriptionDelegate> action(Scenario.Process scenario) {
    return scenario.actsOnMessageIntermediateCatchEvent(getActivityId());
  }

  @Override
  public void receive() {
    getRuntimeService().messageEventReceived(getEventName(), getExecutionId());
  }

  @Override
  public void receive(Map<String, Object> variables) {
    getRuntimeService().messageEventReceived(getEventName(), getExecutionId(), variables);
  }

}
