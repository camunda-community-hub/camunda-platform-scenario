package org.camunda.bpm.scenarios;


import org.camunda.bpm.engine.ProcessEngine;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class EventBasedGatewayWaitstate extends Waitstate<EventBasedGatewayWaitstate.EventBasedGateway> {

  protected EventBasedGatewayWaitstate(ProcessEngine processEngine, String executionId, String activityId) {
    super(processEngine, executionId, activityId);
  }

  protected class EventBasedGateway {

    public MessageEventWaitstate messageEvent(String activityId) {
      return new MessageEventWaitstate(processEngine, executionId, activityId);
    }

    public ReceiveTaskWaitstate receiveTask(String activityId) {
      return new ReceiveTaskWaitstate(processEngine, executionId, activityId);
    }

    public SignalEventWaitstate signalEvent(String activityId) {
      return new SignalEventWaitstate(processEngine, executionId, activityId);
    }

    public TimerEventWaitstate timerEvent(String activityId) {
      return new TimerEventWaitstate(processEngine, executionId, activityId);
    }

  }

  @Override
  protected EventBasedGateway get() {
    return new EventBasedGateway();
  }

  protected void leave() {
    throw new UnsupportedOperationException();
  }

  protected void leave(Map<String, Object> variables) {
    throw new UnsupportedOperationException();
  }

  public MessageEventWaitstate messageEvent(String activityId) {
    return get().messageEvent(activityId);
  }

  public ReceiveTaskWaitstate receiveTask(String activityId) {
    return get().receiveTask(activityId);
  }

  public SignalEventWaitstate signalEvent(String activityId) {
    return get().signalEvent(activityId);
  }

  public TimerEventWaitstate timerEvent(String activityId) {
    return get().timerEvent(activityId);
  }

}
