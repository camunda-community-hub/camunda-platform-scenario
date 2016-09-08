package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.scenario.defer.Deferrable;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface EventSubscriptionDelegate extends EventSubscription, ProcessInstanceAwareDelegate, Deferrable {

  void receive();

  void receive(Map<String, Object> variables);

}
