package org.camunda.bpm.scenario.action;

import org.camunda.bpm.scenario.runner.TimerIntermediateCatchEventWaitstate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface TimerIntermediateCatchEventAction extends ScenarioAction<TimerIntermediateCatchEventWaitstate> {

  @Override
  void execute(TimerIntermediateCatchEventWaitstate job);

}
