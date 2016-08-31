package org.camunda.bpm.scenario.impl.waitstate;


import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.action.ScenarioAction;
import org.camunda.bpm.scenario.delegate.EventSubscriptionDelegate;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;
import org.camunda.bpm.scenario.impl.delegate.AbstractEventSubscriptionDelegate;
import org.camunda.bpm.scenario.impl.delegate.EventSubscriptionDelegateImpl;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class MessageIntermediateCatchEventWaitstate extends AbstractEventSubscriptionDelegate {

  private EventSubscriptionDelegate eventSubscriptionDelegate;

  public MessageIntermediateCatchEventWaitstate(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner, instance);
    eventSubscriptionDelegate = EventSubscriptionDelegateImpl.newInstance(this, delegate);
  }

  @Override
  protected EventSubscription getDelegate() {
    return getRuntimeService().createEventSubscriptionQuery().eventType("message").activityId(getActivityId()).executionId(getExecutionId()).singleResult();
  }

  @Override
  protected ScenarioAction<EventSubscriptionDelegate> action(ProcessScenario scenario) {
    return scenario.waitsAtMessageIntermediateCatchEvent(getActivityId());
  }

  @Override
  public void receive() {
    eventSubscriptionDelegate.receive();
  }

  @Override
  public void receive(Map<String, Object> variables) {
    eventSubscriptionDelegate.receive(variables);
  }

}
