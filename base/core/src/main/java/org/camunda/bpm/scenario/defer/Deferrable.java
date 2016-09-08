package org.camunda.bpm.scenario.defer;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface Deferrable {

  void defer(String period, Deferred action);

}
