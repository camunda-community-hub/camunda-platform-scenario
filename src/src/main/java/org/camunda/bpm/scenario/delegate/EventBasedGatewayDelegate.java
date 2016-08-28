package org.camunda.bpm.scenario.delegate;

import java.util.List;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface EventBasedGatewayDelegate extends ProcessInstanceAwareDelegate {

  List<EventSubscriptionDelegate> getEventSubscriptions();

  EventSubscriptionDelegate getEventSubscription(String activityId);

  EventSubscriptionDelegate getEventSubscription();

}
