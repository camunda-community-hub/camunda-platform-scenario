package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.runtime.EventSubscription;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface EventSubscriptionDelegate extends EventSubscription {

  void receive();

  void receive(Map<String, Object> variables);

}
