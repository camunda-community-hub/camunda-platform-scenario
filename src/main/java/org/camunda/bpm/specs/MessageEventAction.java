package org.camunda.bpm.specs;

import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.ProcessInstance;

public interface MessageEventAction {

  void execute(EventSubscription messageEventSubscription, ProcessInstance processInstance);

}
