package org.camunda.bpm.specs;

import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import java.util.List;
import java.util.Map;

public interface EventBasedGatewayAction {

  void execute(Map<String, EventSubscription> eventSubscriptions, Job timerJob, ProcessInstance processInstance);

}
