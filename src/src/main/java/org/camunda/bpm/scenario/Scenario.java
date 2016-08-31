package org.camunda.bpm.scenario;

import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;
import org.camunda.bpm.scenario.impl.ScenarioExecutorImpl;
import org.camunda.bpm.scenario.runner.ProcessRunner;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class Scenario {

  public static ProcessRunner.ToBeStartedBy run(ProcessScenario scenario) {
    return new ScenarioExecutorImpl(scenario).toBeStartedBy();
  }

  public static ProcessRunner use(ProcessScenario scenario) {
    return new ProcessRunnerImpl(null, scenario);
  }

}
