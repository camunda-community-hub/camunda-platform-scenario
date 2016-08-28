package org.camunda.bpm.scenario.impl.waitstate;


import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.ScenarioAction;
import org.camunda.bpm.scenario.delegate.EventBasedGatewayDelegate;
import org.camunda.bpm.scenario.impl.ExecutableWaitstate;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class EventBasedGatewayWaitstate extends ExecutableWaitstate<EventBasedGatewayDelegate> implements EventBasedGatewayDelegate {

  public EventBasedGatewayWaitstate(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner, instance);
  }

  @Override
  protected EventBasedGatewayDelegate getDelegate() {
    return null;
  }

  @Override
  protected ScenarioAction<EventBasedGatewayDelegate> action(Scenario.Process scenario) {
    return scenario.actsOnEventBasedGateway(getActivityId());
  }

  protected void leave() {
    getRuntimeService().messageEventReceived(getMessageEventSubscription().getEventName(), getMessageEventSubscription().getExecutionId());
  }

  protected void leave(Map<String, Object> variables) {
    getRuntimeService().messageEventReceived(getMessageEventSubscription().getEventName(), getMessageEventSubscription().getExecutionId(), variables);
  }

  @Override
  public EventSubscription getSignalEventSubscription() {
    return getRuntimeService().createEventSubscriptionQuery().eventType("signal").executionId(getExecutionId()).singleResult();
  }

  public EventSubscription getSignalEventSubscription(String activityId) {
    return getRuntimeService().createEventSubscriptionQuery().eventType("signal").activityId(activityId).executionId(getExecutionId()).singleResult();
  }

  @Override
  public EventSubscription getMessageEventSubscription() {
    return getRuntimeService().createEventSubscriptionQuery().eventType("message").executionId(getExecutionId()).singleResult();
  }

  public EventSubscription getMessageEventSubscription(String activityId) {
    return getRuntimeService().createEventSubscriptionQuery().eventType("message").activityId(activityId).executionId(getExecutionId()).singleResult();
  }

  @Override
  public void receiveSignal() {
    EventSubscription subscription = getSignalEventSubscription();
    getRuntimeService().signalEventReceived(subscription.getEventName(), subscription.getExecutionId());
  }

  @Override
  public void receiveSignal(Map<String, Object> variables) {
    EventSubscription subscription = getSignalEventSubscription();
    getRuntimeService().signalEventReceived(subscription.getEventName(), subscription.getExecutionId(), variables);
  }

  public void receiveSignal(String activityId) {
    EventSubscription subscription = getSignalEventSubscription(activityId);
    getRuntimeService().signalEventReceived(subscription.getEventName(), subscription.getExecutionId());
  }

  public void receiveSignal(String activityId, Map<String, Object> variables) {
    EventSubscription subscription = getSignalEventSubscription(activityId);
    getRuntimeService().signalEventReceived(subscription.getEventName(), subscription.getExecutionId(), variables);
  }

  @Override
  public void receiveMessage() {
    leave();
  }

  @Override
  public void receiveMessage(Map<String, Object> variables) {
    leave(variables);
  }

  public void receiveMessage(String activityId) {
    EventSubscription subscription = getMessageEventSubscription(activityId);
    getRuntimeService().messageEventReceived(subscription.getEventName(), subscription.getExecutionId());
  }

  public void receiveMessage(String activityId, Map<String, Object> variables) {
    EventSubscription subscription = getMessageEventSubscription(activityId);
    getRuntimeService().messageEventReceived(subscription.getEventName(), subscription.getExecutionId(), variables);
  }

}
