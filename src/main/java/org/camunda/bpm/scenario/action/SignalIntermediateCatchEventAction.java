package org.camunda.bpm.scenario.action;

import org.camunda.bpm.scenario.runner.SignalIntermediateCatchEventWaitstate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface SignalIntermediateCatchEventAction extends ScenarioAction<SignalIntermediateCatchEventWaitstate> {

  @Override
  void execute(SignalIntermediateCatchEventWaitstate eventSubscription);

}
