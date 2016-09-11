package org.camunda.bpm.scenario;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;
import org.camunda.bpm.scenario.impl.ScenarioImpl;
import org.camunda.bpm.scenario.run.ProcessRunner;
import org.camunda.bpm.scenario.run.ProcessRunner.StartableRunner;

import java.util.List;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class Scenario {

  /**
   * Run a new process instance by means of the scenario interface
   * provided as parameter.
   * .
   * @param scenario interface to be used for running the process
   * instance
   */
  public static StartableRunner run(ProcessScenario scenario) {
    return new ScenarioImpl(scenario).toBeStartedBy();
  }

  /**
   * Integrate an existing process instance (e.g. created by a Call
   * Activity) into a scenario run by means of the scenario interface
   * provided as parameter.
   *
   * @param scenario interface to be used for running the process
   * instance
   */
  public static ProcessRunner use(ProcessScenario scenario) {
    return new ProcessRunnerImpl(null, scenario);
  }

  /**
   * Retrieve the process instance run by the scenario provided as
   * parameter.
   *
   * @param scenario for which the process instance should be delivered
   * @return the process instance run by that scenario.
   * @throws IllegalStateException in case the scenario executed more
   * than a single process instance based on the scenario provided as
   * a parameter.
   */
  public abstract ProcessInstance instance(ProcessScenario scenario);

  /**
   * Retrieve the process instances run by the scenario provided as
   * parameter.
   *
   * @param scenario for which the process instances should be delivered
   * @return the process instances run by that scenario in the order in
   * which they were added to the scenario during the scenario runtime.
   */
  public abstract List<ProcessInstance> instances(ProcessScenario scenario);

}
