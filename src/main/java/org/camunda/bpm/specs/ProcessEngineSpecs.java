package org.camunda.bpm.specs;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ProcessEngineSpecs {

  public static ScenarioRunner run(Scenario scenario) {
    return new ScenarioRunner(scenario);
  }

}
