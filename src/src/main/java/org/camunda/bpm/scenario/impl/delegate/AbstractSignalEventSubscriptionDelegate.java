package org.camunda.bpm.scenario.impl.delegate;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.scenario.delegate.SignalEventSubscriptionDelegate;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;
import org.camunda.bpm.scenario.impl.ExecutableWaitstate;

import java.util.Date;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class AbstractSignalEventSubscriptionDelegate extends ExecutableWaitstate<EventSubscription> implements SignalEventSubscriptionDelegate {

  public AbstractSignalEventSubscriptionDelegate(ProcessRunnerImpl runner, HistoricActivityInstance instance, String duration) {
    super(runner, instance, duration);
  }

  public String getId() {
    return runtimeDelegate.getId();
  }

  public String getEventType() {
    return runtimeDelegate.getEventType();
  }

  public String getEventName() {
    return runtimeDelegate.getEventName();
  }

  public String getProcessInstanceId() {
    return runtimeDelegate.getProcessInstanceId();
  }

  public String getTenantId() {
    return runtimeDelegate.getTenantId();
  }

  public Date getCreated() {
    return runtimeDelegate.getCreated();
  }

}
