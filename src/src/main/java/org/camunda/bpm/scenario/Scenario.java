package org.camunda.bpm.scenario;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;
import org.camunda.bpm.scenario.impl.ScenarioImpl;
import org.camunda.bpm.scenario.run.ProcessRunner;

import java.util.List;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class Scenario {

  public static ProcessRunner.ToBeStartedBy run(ProcessScenario scenario) {
    return new ScenarioImpl(scenario).toBeStartedBy();
  }

  public static ProcessRunner use(ProcessScenario scenario) {
    return new ProcessRunnerImpl(null, scenario);
  }

  public abstract ProcessInstance getInstance(ProcessScenario scenario);

  public abstract List<ProcessInstance> getInstances(ProcessScenario scenario);

}
