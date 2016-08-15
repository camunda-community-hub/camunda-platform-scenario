package org.camunda.bpm.specs;


/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface Scenario {

  WaitstateAction<TaskWaitstate> atTask(String activityId);

  WaitstateAction<ExternalTaskWaitstate> atExternalTask(String activityId);

  WaitstateAction<TimerEventWaitstate> atTimerEvent(String activityId);

  WaitstateAction<MessageEventWaitstate> atMessageEvent(String activityId);

  WaitstateAction<ReceiveTaskWaitstate> atReceiveTask(String activityId);

  WaitstateAction<SignalEventWaitstate> atSignalEvent(String activityId);

  WaitstateAction<EventBasedGatewayWaitstate> atEventBasedGateway(String activityId);

  Scenario startsCallActivity(String activityId);

  Scenario startsProcessInstance(String activityId);

}
