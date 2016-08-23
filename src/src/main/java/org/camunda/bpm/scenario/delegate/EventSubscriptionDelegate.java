package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.scenario.runner.Waitstate;

import java.util.Date;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class EventSubscriptionDelegate extends Waitstate<EventSubscription> implements EventSubscription {

  public EventSubscriptionDelegate(ProcessEngine processEngine, HistoricActivityInstance instance, String duration) {
    super(processEngine, instance, duration);
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
