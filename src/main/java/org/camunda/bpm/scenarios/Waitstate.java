package org.camunda.bpm.scenarios;

import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.*;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class Waitstate<O> extends Savepoint<O> {

  protected HistoricActivityInstance instance;

  protected Waitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine);
    this.instance = instance;
  }

  @Override
  protected String getExecutionId() {
    return instance.getExecutionId();
  }

  protected String getActivityId() {
    return instance.getActivityId();
  }

  protected static Waitstate newInstance(ProcessEngine processEngine, HistoricActivityInstance instance) {
    if (CallActivityWaitstate.getActivityType().equals(instance.getActivityType())) {
      return new CallActivityWaitstate(processEngine, instance);
    } else if (EventBasedGatewayWaitstate.getActivityType().equals(instance.getActivityType())) {
      return new EventBasedGatewayWaitstate(processEngine, instance);
    } else if (ExternalTaskWaitstate.getActivityTypes().contains(instance.getActivityType())) {
      return new ExternalTaskWaitstate(processEngine, instance);
    } else if (MessageEventWaitstate.getActivityType().equals(instance.getActivityType())) {
      return new MessageEventWaitstate(processEngine, instance);
    } else if (ReceiveTaskWaitstate.getActivityType().equals(instance.getActivityType())) {
      return new ReceiveTaskWaitstate(processEngine, instance);
    } else if (SignalEventWaitstate.getActivityType().equals(instance.getActivityType())) {
      return new SignalEventWaitstate(processEngine, instance);
    } else if (TaskWaitstate.getActivityType().equals(instance.getActivityType())) {
      return new TaskWaitstate(processEngine, instance);
    } else if (TimerEventWaitstate.getActivityType().equals(instance.getActivityType())) {
      return new TimerEventWaitstate(processEngine, instance);
    } else {
      return new IgnoredWaitstate(processEngine, instance);
    }
  }

  protected abstract void execute(Scenario scenario);

  protected abstract void leave();

  protected abstract void leave(Map<String, Object> variables);

  protected boolean unfinished() {
    return getHistoryService().createHistoricActivityInstanceQuery().activityInstanceId(instance.getId()).unfinished().singleResult() != null;
  }

  public SignalEventReceivedBuilder createSignal(String signalName) {
    return getRuntimeService().createSignalEvent(signalName);
  }

  public MessageCorrelationBuilder createMessage(String messageName) {
    return getRuntimeService().createMessageCorrelation(messageName);
  }

  public void triggerTimer(String activityId) {
    Job job = getManagementService().createJobQuery().processInstanceId(getProcessInstance().getId()).activityId(activityId).timers().singleResult();
    getManagementService().executeJob(job.getId());
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof Waitstate && getActivityId().equals(((Waitstate) o).getActivityId());
  }

}
