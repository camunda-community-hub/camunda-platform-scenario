package org.camunda.bpm.scenario.runner;


import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.ScenarioAction;

import java.util.Date;
import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ReceiveTaskWaitstate extends MessageIntermediateCatchEventWaitstate {

  public ReceiveTaskWaitstate(ProcessRunnerImpl runner, HistoricActivityInstance instance, String duration) {
    super(runner, instance, duration);
  }

  @Override
  protected ScenarioAction action(Scenario.Process scenario) {
    return scenario.atReceiveTask(getActivityId());
  }

  @Override
  protected EventSubscription getRuntimeDelegate() {
    return getRuntimeService().createEventSubscriptionQuery().eventType("message").executionId(getExecutionId()).singleResult();
  }

  @Override
  protected void leave() {
    EventSubscription eventSubscription = getRuntimeDelegate();
    if (eventSubscription != null) {
      getRuntimeService().messageEventReceived(getRuntimeDelegate().getEventName(), getRuntimeDelegate().getExecutionId());
    } else {
      getRuntimeService().signal(getExecutionId());
    }
  }

  @Override
  protected void leave(Map<String, Object> variables) {
    EventSubscription eventSubscription = getRuntimeDelegate();
    if (eventSubscription != null) {
      getRuntimeService().messageEventReceived(getRuntimeDelegate().getEventName(), getRuntimeDelegate().getExecutionId(), variables);
    } else {
      getRuntimeService().signal(getExecutionId());
    }
  }

  @Override
  public void receiveMessage() {
    super.receiveMessage();
  }

  @Override
  public void receiveMessage(Map<String, Object> variables) {
    super.receiveMessage(variables);
  }

  @Override
  public String getId() {
    return super.getId();
  }

  @Override
  public String getEventType() {
    if (runtimeDelegate == null)
      throw new UnsupportedOperationException("Not supported for Receive Tasks " +
          "used without a message event subscription.");
      return super.getEventType();
  }

  @Override
  public String getEventName() {
    if (runtimeDelegate == null)
      throw new UnsupportedOperationException("Not supported for Receive Tasks " +
          "used without a message event subscription.");
    return super.getEventName();
  }

  @Override
  public String getProcessInstanceId() {
    if (runtimeDelegate == null)
      throw new UnsupportedOperationException("Not supported for Receive Tasks " +
          "used without a message event subscription.");
    return super.getProcessInstanceId();
  }

  @Override
  public String getTenantId() {
    if (runtimeDelegate == null)
      throw new UnsupportedOperationException("Not supported for Receive Tasks " +
          "used without a message event subscription.");
    return super.getTenantId();
  }

  @Override
  public Date getCreated() {
    if (runtimeDelegate == null)
      throw new UnsupportedOperationException("Not supported for Receive Tasks " +
          "used without a message event subscription.");
    return super.getCreated();
  }

}
