package org.camunda.bpm.specs;

import org.camunda.bpm.engine.test.assertions.ProcessEngineTests;

/**
 * Created by martin on 05.08.16.
 */
public class ProcessEngineSpecs extends ProcessEngineTests {

  public static ScenarioRunner run(Scenario scenario) {
    return new ScenarioRunner(scenario);
  }

}
