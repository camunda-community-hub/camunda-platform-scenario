package org.camunda.bpm.scenario.action;


/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface DeferredAction {

  void execute() throws Exception;

}
