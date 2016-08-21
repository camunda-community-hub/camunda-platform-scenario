package org.camunda.bpm.scenario.action;


import org.camunda.bpm.scenario.runner.Waitstate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ScenarioAction<W extends Waitstate> {

  void execute(W runtimeInstance);

}
