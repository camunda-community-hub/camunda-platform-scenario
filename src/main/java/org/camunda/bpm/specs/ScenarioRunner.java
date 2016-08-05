package org.camunda.bpm.specs;

import org.camunda.bpm.engine.runtime.ProcessInstance;

/**
 * Created by martin on 05.08.16.
 */
public class ScenarioRunner {

  private Scenario scenario;

  ScenarioRunner(Scenario scenario) {
    this.scenario = scenario;
  }

  ScenarioRunner fromStart() {
    return this;
  }

  ScenarioRunner fromBefore(String activityId, String... activityIds) {
    return this;
  }

  ScenarioRunner fromAfter(String activityId, String... activityIds) {
    return this;
  }

  ScenarioRunner fromStart(StartAction action) {
    return this;
  }

  ScenarioRunner toBefore(String activityId, String... activityIds) {
    return this;
  }

  ScenarioRunner toAfter(String activityId, String... activityIds) {
    return this;
  }

  ScenarioRunner toEnd() {
    return this;
  }

  ProcessInstance go() { return null; }

}
