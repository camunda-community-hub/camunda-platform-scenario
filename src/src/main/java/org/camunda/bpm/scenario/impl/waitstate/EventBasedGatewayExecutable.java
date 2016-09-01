package org.camunda.bpm.scenario.impl.waitstate;


import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.act.Action;
import org.camunda.bpm.scenario.delegate.EventBasedGatewayDelegate;
import org.camunda.bpm.scenario.delegate.EventSubscriptionDelegate;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;
import org.camunda.bpm.scenario.impl.WaitstateExecutable;
import org.camunda.bpm.scenario.impl.delegate.EventSubscriptionDelegateImpl;

import java.util.List;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class EventBasedGatewayExecutable extends WaitstateExecutable<EventBasedGatewayDelegate> implements EventBasedGatewayDelegate {

  public EventBasedGatewayExecutable(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner, instance);
  }

  @Override
  protected EventBasedGatewayDelegate getDelegate() {
    return null;
  }

  @Override
  protected Action<EventBasedGatewayDelegate> action(ProcessScenario scenario) {
    return scenario.waitsAtEventBasedGateway(getActivityId());
  }

  @Override
  public List<EventSubscriptionDelegate> getEventSubscriptions() {
    List<EventSubscription> eventSubscriptions = getRuntimeService().createEventSubscriptionQuery().executionId(getExecutionId()).list();
    return EventSubscriptionDelegateImpl.newInstance(this, eventSubscriptions);
  }

  @Override
  public EventSubscriptionDelegate getEventSubscription(String activityId) {
    return EventSubscriptionDelegateImpl.newInstance(this, getRuntimeService().createEventSubscriptionQuery().activityId(activityId).executionId(getExecutionId()).singleResult());
  }

  @Override
  public EventSubscriptionDelegate getEventSubscription() {
    return EventSubscriptionDelegateImpl.newInstance(this, getRuntimeService().createEventSubscriptionQuery().executionId(getExecutionId()).singleResult());
  }

}
