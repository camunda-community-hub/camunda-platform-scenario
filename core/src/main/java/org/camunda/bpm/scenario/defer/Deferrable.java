package org.camunda.bpm.scenario.defer;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface Deferrable {

  /**
   * Defer a certain action for a certain period of time until
   * it is executed. Note that the action is just executed as
   * long as *this* runtime object for which you defer the action
   * still exists in the runtime database. If it disappears before
   * the deferred action triggers, the action will never be executed.
   *
   * @param period of time for which you want to defer an action
   * @param action which should be executed after the given period.
   */
  void defer(String period, Deferred action);

}
