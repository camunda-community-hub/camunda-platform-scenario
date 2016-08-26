package org.camunda.bpm.scenario.action;

import org.camunda.bpm.scenario.delegate.TimerJobDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface TimerIntermediateCatchEventAction extends ScenarioAction<TimerJobDelegate> {

  @Override
  void execute(TimerJobDelegate job);

}
