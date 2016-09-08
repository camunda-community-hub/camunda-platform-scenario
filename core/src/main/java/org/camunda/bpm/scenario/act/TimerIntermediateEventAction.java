package org.camunda.bpm.scenario.act;

import org.camunda.bpm.scenario.delegate.ProcessInstanceDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface TimerIntermediateEventAction extends Action<ProcessInstanceDelegate> {

  @Override
  void execute(final ProcessInstanceDelegate processInstance) throws Exception;

}
