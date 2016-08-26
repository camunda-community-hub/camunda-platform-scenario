package org.camunda.bpm.scenario.runner;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ProcessRunner extends CallActivityRunner {

  ProcessRunner startBy(String processDefinitionKey);

  ProcessRunner startBy(String processDefinitionKey, Map<String, Object> variables);

  ProcessRunner startBy(ProcessStarter starter);

  ProcessRunner toBefore(String activityId);

  ProcessRunner toAfter(String activityId);

  ProcessRunner fromBefore(String activityId);

  ProcessRunner fromAfter(String activityId);

  ProcessRunner engine(ProcessEngine processEngine);

  ProcessInstance execute();

}
