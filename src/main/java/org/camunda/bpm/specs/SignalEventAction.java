package org.camunda.bpm.specs;

import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.ProcessInstance;

public interface SignalEventAction {

  void execute(EventSubscription signalEventSubscription, ProcessInstance processInstance);

}
