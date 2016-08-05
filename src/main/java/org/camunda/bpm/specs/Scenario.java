package org.camunda.bpm.specs;

import org.camunda.bpm.engine.task.Task;

/**
 * Created by martin on 05.08.16.
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
