package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.runtime.EventSubscription;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface MessageEventSubscriptionDelegate extends EventSubscription {

  void receiveMessage();

  void receiveMessage(Map<String, Object> variables);

}
