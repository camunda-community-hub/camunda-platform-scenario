package org.camunda.bpm.scenario.impl.delegate;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.scenario.delegate.EventSubscriptionDelegate;
import org.camunda.bpm.scenario.impl.ExecutableWaitstate;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;

import java.util.Date;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class AbstractEventSubscriptionDelegate extends ExecutableWaitstate<EventSubscription> implements EventSubscriptionDelegate {

  public AbstractEventSubscriptionDelegate(ProcessRunnerImpl runner, HistoricActivityInstance instance, String duration) {
    super(runner, instance, duration);
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

  public String getProcessInstanceId() {
    return delegate.getProcessInstanceId();
  }

  public String getTenantId() {
    return delegate.getTenantId();
  }

  public Date getCreated() {
    return delegate.getCreated();
  }

}
