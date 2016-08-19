package org.camunda.bpm.scenarios;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenarios.runner.ScenarioRunnerImpl;

import java.util.Map;

public interface ScenarioRunner {

  ScenarioRunner variables(Map<String, Object> variables);

  ScenarioRunner fromBefore(String activityId, String... activityIds);

  ScenarioRunner fromAfter(String activityId, String... activityIds);

  ScenarioRunner toBefore(String activityId, String... activityIds);

  ScenarioRunner toAfter(String activityId, String... activityIds);

  ProcessInstance run(Scenario scenario);

  ProcessInstance run(Scenario scenario, ProcessEngine processEngine);

}
