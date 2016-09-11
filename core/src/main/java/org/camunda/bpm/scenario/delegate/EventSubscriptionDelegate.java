package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.scenario.defer.Deferrable;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface EventSubscriptionDelegate extends EventSubscription, ProcessInstanceAwareDelegate, Deferrable {

  /**
   * Receive the event the event subscription is waiting for.
   */
  void receive();

  /**
   * Receive the event the event subscription is waiting for
   * and deliver a map of received information to be stored
   * as process instance variables.
   */
  void receive(Map<String, Object> variables);

}
