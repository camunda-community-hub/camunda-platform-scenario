package org.camunda.bpm.scenario.defer;


/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface Deferred {

  void execute() throws Exception;

}
