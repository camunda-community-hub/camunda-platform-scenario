package org.camunda.bpm.scenario.action;

import org.camunda.bpm.scenario.delegate.ProcessInstanceDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface TimerIntermediateEventAction extends ScenarioAction<ProcessInstanceDelegate> {

  @Override
  void execute(final ProcessInstanceDelegate processInstance);

}
