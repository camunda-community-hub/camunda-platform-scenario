package org.camunda.bpm.scenario.act;

import org.camunda.bpm.scenario.delegate.ProcessInstanceDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface TimerIntermediateEventAction extends Action<ProcessInstanceDelegate> {

  /**
   * Implement this action with custom code to be executed when the
   * process instance arrives at a timer intermediate event. Note
   * that you do not need to implement this action at all or that
   * you could decide to implement with an empty body, because timer
   * jobs will implicitely trigger during scenario execution. However
   * you still may want to intercept that waitstate with an alternative
   * action that happens while you wait here, e.g. a message arriving
   * for an event based sub process.
   *
   * @param processInstance the timer event is defined for.
   *
   * @throws Exception in case your custom code throws checked exceptions.
   * Such exceptions will be wrapped into RuntimeExceptions and rethrown.
   */
  @Override
  void execute(final ProcessInstanceDelegate processInstance) throws Exception;

}
