package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.scenario.defer.Deferrable;

import java.util.List;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface EventBasedGatewayDelegate extends ProcessInstanceAwareDelegate, Deferrable {

  /**
   * Get all the event subscriptions defined for this event based gateway.
   *
   * @return the list of event subscriptions defined for this gateway.
   */
  List<EventSubscriptionDelegate> getEventSubscriptions();

  /**
   * Get the event subscriptions defined for this event based gateway
   * which is defined by the event (or receive task) symbol with the
   * activity id provided as parameter.
   *
   * @param activityId the activity id of the requested event subscription's
   * event (or receive task) symbol
   * @return the list of event subscriptions defined for this gateway.
   */
  EventSubscriptionDelegate getEventSubscription(String activityId);

  /**
   * Get the one and only event subscriptions defined for this event
   * based gateway.
   *
   * @return the event subscription defined for this gateway.
   * @throws org.camunda.bpm.engine.ProcessEngineException in case more
   * than one such event subscription is defined for this event based
   * gateway.
   */
  EventSubscriptionDelegate getEventSubscription();

}
