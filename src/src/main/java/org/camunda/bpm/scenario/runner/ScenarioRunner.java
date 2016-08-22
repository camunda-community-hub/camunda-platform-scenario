package org.camunda.bpm.scenario.runner;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.Scenario;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ScenarioRunner {

  ScenarioRunner variables(Map<String, Object> variables);

  ScenarioRunner fromBefore(String activityId, String... activityIds);

  ScenarioRunner fromAfter(String activityId, String... activityIds);

  ScenarioRunner toBefore(String activityId, String... activityIds);

  ScenarioRunner toAfter(String activityId, String... activityIds);

  ScenarioRunner engine(ProcessEngine processEngine);

  ProcessInstance execute(Scenario.Bpmn scenario);

}
