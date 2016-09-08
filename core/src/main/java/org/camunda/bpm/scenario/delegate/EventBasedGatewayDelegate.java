package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.scenario.defer.Deferrable;

import java.util.List;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface EventBasedGatewayDelegate extends ProcessInstanceAwareDelegate, Deferrable {

  List<EventSubscriptionDelegate> getEventSubscriptions();

  EventSubscriptionDelegate getEventSubscription(String activityId);

  EventSubscriptionDelegate getEventSubscription();

}
