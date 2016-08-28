package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.Job;

import java.util.List;
import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface EventBasedGatewayDelegate {

  List<EventSubscriptionDelegate> getEventSubscriptions();

  EventSubscriptionDelegate getEventSubscription(String activityId);

  EventSubscriptionDelegate getEventSubscription();

}
