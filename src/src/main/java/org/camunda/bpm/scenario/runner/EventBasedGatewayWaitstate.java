package org.camunda.bpm.scenario.runner;


import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.ScenarioAction;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class EventBasedGatewayWaitstate extends Waitstate<EventBasedGateway> implements EventBasedGateway {

  public EventBasedGatewayWaitstate(ProcessRunnerImpl runner, HistoricActivityInstance instance, String duration) {
    super(runner, instance, duration);
  }

  @Override
  protected void execute() {
    Job job = getTimer();
    if (job == null) {
      super.execute();
    } else {
      ScenarioAction action = action(runner.scenario);
      if (action != null)
        action.execute(this);
      job = getManagementService().createJobQuery().timers().jobId(job.getId()).singleResult();
      if (job != null)
        getManagementService().executeJob(job.getId());
      runner.setExecutedHistoricActivityIds(historicDelegate);
    }
  }

  @Override
  protected EventBasedGateway getRuntimeDelegate() {
    return new EventBasedGateway() {};
  }

  @Override
  protected ScenarioAction<EventBasedGatewayWaitstate> action(Scenario.Process scenario) {
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

  public Job getTimer() {
    List<Job> jobs = getManagementService().createJobQuery().timers().executionId(getExecutionId()).orderByJobDuedate().asc().listPage(0, 1);
    return jobs.isEmpty() ? null : jobs.get(0);
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

  @Override
  protected Date getEndTime() {
    Job timer = getTimer();
    if (timer != null) {
      if (duration != null) {
        throw new IllegalStateException("Found a duration '" + duration + "' set. " +
          "Explicit durations are not supported for '" + getClass().getSimpleName() +
          "' with timer events. Its duration always depends on the timer defined " +
          "in the BPMN process.");
      }
      return timer.getDuedate();
    }
    return super.getEndTime();
  }

}
