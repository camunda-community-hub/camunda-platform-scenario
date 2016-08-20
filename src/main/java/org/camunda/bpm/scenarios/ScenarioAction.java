package org.camunda.bpm.scenarios;


import org.camunda.bpm.scenarios.runner.Waitstate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ScenarioAction<W extends Waitstate> {

  void execute(W runtimeInstance);

}
