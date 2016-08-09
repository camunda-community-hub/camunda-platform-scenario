package org.camunda.bpm.specs;

import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.ProcessInstance;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface SignalEventAction {

  void execute(EventSubscription signalEventSubscription, ProcessInstance processInstance);

}
