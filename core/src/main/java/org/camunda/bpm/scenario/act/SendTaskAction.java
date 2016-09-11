package org.camunda.bpm.scenario.act;

import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface SendTaskAction extends ServiceTaskAction {

  /**
   * Implement this action with custom code to be executed when the
   * process instance arrives at a send task (external task waitstate).
   *
   * @param externalTask the send task is implemented with.
   *
   * @throws Exception in case your custom code throws checked exceptions.
   * Such exceptions will be wrapped into RuntimeExceptions and rethrown.
   */
  @Override
  void execute(final ExternalTaskDelegate externalTask) throws Exception;

}
