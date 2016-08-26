package org.camunda.bpm.scenario.delegate;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface SignalEventSubscriptionDelegate {

  void receiveSignal();

  void receiveSignal(Map<String, Object> variables);

}
