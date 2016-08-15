package org.camunda.bpm.specs;

import org.camunda.bpm.engine.runtime.ProcessInstance;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ScenarioRunner {

  private Scenario scenario;

  public ScenarioRunner(Scenario scenario) {
    this.scenario = scenario;
  }

  public ScenarioRunner fromStart() {
    return this;
  }

  public ScenarioRunner fromBefore(String activityId, String... activityIds) {
    return this;
  }

  public ScenarioRunner fromAfter(String activityId, String... activityIds) {
    return this;
  }

  public ScenarioRunner fromStart(StartAction action) {
    return this;
  }

  public ScenarioRunner toBefore(String activityId, String... activityIds) {
    return this;
  }

  public ScenarioRunner toAfter(String activityId, String... activityIds) {
    return this;
  }

  public ScenarioRunner toEnd() {
    return this;
  }

  public ProcessInstance run() { return null; }

}
