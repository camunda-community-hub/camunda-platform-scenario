package org.camunda.bpm.specs;


/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface Scenario {

  TaskAction atTask(String activityId);

  ExternalTaskAction atExternalTask(String activityId);

  TimerEventAction atTimerEvent(String activityId);

  MessageEventAction atMessageEvent(String activityId);

  SignalEventAction atSignalEvent(String activityId);

  EventBasedGatewayAction atEventBasedGateway(String activityId);

  Scenario startsCallActivity(String activityId);

  Scenario startsProcessInstance(String activityId);

}
