package org.camunda.bpm.scenario.runner;


import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.ScenarioAction;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class EventBasedGatewayWaitstate extends Waitstate<EventBasedGateway> implements EventBasedGateway {

  protected EventBasedGatewayWaitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine, instance);
  }

  @Override
  protected EventBasedGateway getRuntimeDelegate() {
    return new EventBasedGateway() {};
  }

  @Override
  protected ScenarioAction<EventBasedGatewayWaitstate> action(Scenario scenario) {
    return scenario.atEventBasedGateway(getActivityId());
  }

  protected void leave() {
    getRuntimeService().messageEventReceived(getMessageEventSubscription().getEventName(), getMessageEventSubscription().getExecutionId());
  }

  protected void leave(Map<String, Object> variables) {
    getRuntimeService().messageEventReceived(getMessageEventSubscription().getEventName(), getMessageEventSubscription().getExecutionId(), variables);
  }

  public EventSubscription getSignalEventSubscription() {
    return getRuntimeService().createEventSubscriptionQuery().eventType("signal").executionId(getExecutionId()).singleResult();
  }

  public EventSubscription getSignalEventSubscription(String activityId) {
    return getRuntimeService().createEventSubscriptionQuery().eventType("signal").activityId(activityId).executionId(getExecutionId()).singleResult();
  }

  public EventSubscription getMessageEventSubscription() {
    return getRuntimeService().createEventSubscriptionQuery().eventType("message").executionId(getExecutionId()).singleResult();
  }

  public EventSubscription getMessageEventSubscription(String activityId) {
    return getRuntimeService().createEventSubscriptionQuery().eventType("message").activityId(activityId).executionId(getExecutionId()).singleResult();
  }

  public Job getTimer(String activityId) {
    return getManagementService().createJobQuery().timers().activityId(activityId).executionId(getExecutionId()).singleResult();
  }

  public Job getTimer() {
    return getManagementService().createJobQuery().timers().executionId(getExecutionId()).singleResult();
  }

  public void receiveSignal() {
    EventSubscription subscription = getSignalEventSubscription();
    getRuntimeService().signalEventReceived(subscription.getEventName(), subscription.getExecutionId());
  }

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

  public void receiveMessage() {
    leave();
  }

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

  protected void triggerTimer() {
    getManagementService().executeJob(getTimer().getId());
  }

}
