package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.Job;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface EventBasedGatewayDelegate {

  EventSubscription getSignalEventSubscription();

  EventSubscription getMessageEventSubscription();

  void receiveSignal();

  void receiveSignal(Map<String, Object> variables);

  void receiveMessage();

  void receiveMessage(Map<String, Object> variables);

}
