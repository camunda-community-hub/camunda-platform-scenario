package org.camunda.bpm.scenario.defer;


/**
 * @author <a href="martin.schimak@plexiti.com">Martin Schimak</a>
 */
public interface Deferred {

  /**
   * Implement this method to define the action which should
   * be executed after a certain period of time.
   *
   * @throws Exception in case your custom code throws checked exceptions.
   * Such exceptions will be wrapped into RuntimeExceptions and rethrown.
   */
  void execute() throws Exception;

}
