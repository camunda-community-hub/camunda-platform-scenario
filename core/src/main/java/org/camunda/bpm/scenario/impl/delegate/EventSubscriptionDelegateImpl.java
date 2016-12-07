package org.camunda.bpm.scenario.impl.delegate;

import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.scenario.defer.Deferred;
import org.camunda.bpm.scenario.delegate.EventSubscriptionDelegate;
import org.camunda.bpm.scenario.delegate.ProcessInstanceDelegate;
import org.camunda.bpm.scenario.impl.WaitstateExecutable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class EventSubscriptionDelegateImpl extends AbstractDelegate<EventSubscription> implements EventSubscriptionDelegate {

  WaitstateExecutable waitstate;

  protected EventSubscriptionDelegateImpl(WaitstateExecutable waitstate, EventSubscription eventSubscription) {
    super(eventSubscription);
    this.waitstate = waitstate;
  }

  public static EventSubscriptionDelegate newInstance(WaitstateExecutable waitstate, EventSubscription eventSubscription) {
    return eventSubscription != null ? new EventSubscriptionDelegateImpl(waitstate, eventSubscription) : null;
  }

  public static List<EventSubscriptionDelegate> newInstance(WaitstateExecutable waitstate, List<EventSubscription> eventSubscriptions) {
    List<EventSubscriptionDelegate> delegates = new ArrayList<EventSubscriptionDelegate>();
    for (EventSubscription eventSubscription: eventSubscriptions) {
      delegates.add(newInstance(waitstate, eventSubscription));
    }
    return delegates;
  }

  @Override
  public ProcessInstanceDelegate getProcessInstance() {
    return ProcessInstanceDelegateImpl.newInstance(waitstate, waitstate.getRuntimeService().createProcessInstanceQuery().processInstanceId(delegate.getProcessInstanceId()).singleResult());
  }

  @Override
  public void defer(String period, Deferred action) {
    waitstate.defer(period, action);
  }

  @Override
  public void receive() {
    if (getEventType().equals("message")) {
      waitstate.getRuntimeService().messageEventReceived(getEventName(), getExecutionId());
    } else {
      waitstate.getRuntimeService().signalEventReceived(getEventName(), getExecutionId());
    }
  }

  @Override
  public void receive(Map<String, Object> variables) {
    if (getEventType().equals("message")) {
      waitstate.getRuntimeService().messageEventReceived(getEventName(), getExecutionId(), variables);
    } else {
      waitstate.getRuntimeService().signalEventReceived(getEventName(), getExecutionId(), variables);
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
