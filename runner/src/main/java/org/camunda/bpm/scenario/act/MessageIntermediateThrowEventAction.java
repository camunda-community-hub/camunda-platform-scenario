package org.camunda.bpm.scenario.act;

import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;

/**
 * @author <a href="martin.schimak@plexiti.com">Martin Schimak</a>
 */
public interface MessageIntermediateThrowEventAction extends Action<ExternalTaskDelegate> {

  /**
   * Implement this action with custom code to be executed when the
   * process instance arrives at a message intermediate throw event
   * (external task waitstate).
   *
   * @param externalTask the message intermediate throw event is
   * implemented with.
   *
   * @throws Exception in case your custom code throws checked exceptions.
   * Such exceptions will be wrapped into RuntimeExceptions and rethrown.
   */
  @Override
  void execute(final ExternalTaskDelegate externalTask) throws Exception;

}
