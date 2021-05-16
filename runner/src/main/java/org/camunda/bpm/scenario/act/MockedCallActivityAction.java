package org.camunda.bpm.scenario.act;

import org.camunda.bpm.scenario.delegate.MockedCallActivityDelegate;

/**
 * @author Martin Schimak
 */
public interface MockedCallActivityAction extends Action<MockedCallActivityDelegate> {

  /**
   * Implement this action with custom code to be executed when the
   * process instance arrives at a mocked call activity.
   *
   * @param callActivity the external task the call activity internally
   *                     is mocked with.
   * @throws Exception in case your custom code throws checked exceptions.
   *                   Such exceptions will be wrapped into RuntimeExceptions and rethrown.
   */
  void execute(final MockedCallActivityDelegate callActivity) throws Exception;

}
