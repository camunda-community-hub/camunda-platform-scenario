package org.camunda.bpm.scenario.impl.delegate;

import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.delegate.EventSubscriptionDelegate;
import org.camunda.bpm.scenario.delegate.ProcessInstanceDelegate;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class EventSubscriptionDelegateImpl extends AbstractDelegate<EventSubscription> implements EventSubscriptionDelegate {

  ProcessRunnerImpl runner;

  protected EventSubscriptionDelegateImpl(ProcessRunnerImpl runner, EventSubscription eventSubscription) {
    super(eventSubscription);
    this.runner = runner;
  }

  public static EventSubscriptionDelegate newInstance(ProcessRunnerImpl runner, EventSubscription eventSubscription) {
    return eventSubscription != null ? new EventSubscriptionDelegateImpl(runner, eventSubscription) : null;
  }

  public static List<EventSubscriptionDelegate> newInstance(ProcessRunnerImpl runner, List<EventSubscription> eventSubscriptions) {
    List<EventSubscriptionDelegate> delegates = new ArrayList<EventSubscriptionDelegate>();
    for (EventSubscription eventSubscription: eventSubscriptions) {
      delegates.add(newInstance(runner, eventSubscription));
    }
    return delegates;
  }

  @Override
  public ProcessInstanceDelegate getProcessInstance() {
    return ProcessInstanceDelegateImpl.newInstance(runner, runner.engine().getRuntimeService().createProcessInstanceQuery().processInstanceId(delegate.getProcessInstanceId()).singleResult());
  }

  @Override
  public void receive() {
    if (getEventType().equals("message")) {
      runner.engine().getRuntimeService().messageEventReceived(getEventName(), getExecutionId());
    } else {
      runner.engine().getRuntimeService().signalEventReceived(getEventName(), getExecutionId());
    }
  }

  @Override
  public void receive(Map<String, Object> variables) {
    if (getEventType().equals("message")) {
      runner.engine().getRuntimeService().messageEventReceived(getEventName(), getExecutionId());
    } else {
      runner.engine().getRuntimeService().signalEventReceived(getEventName(), getExecutionId());
    }
  }

  public String getId() {
    return delegate.getId();
  }

  public String getEventType() {
    return delegate.getEventType();
  }

  public String getEventName() {
    return delegate.getEventName();
  }

  public String getExecutionId() {
    return delegate.getExecutionId();
  }

  public String getProcessInstanceId() {
    return delegate.getProcessInstanceId();
  }

  public String getActivityId() {
    return delegate.getActivityId();
  }

  public String getTenantId() {
    return delegate.getTenantId();
  }

  public Date getCreated() {
    return delegate.getCreated();
  }

}
