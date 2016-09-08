package org.camunda.bpm.scenario.act;


/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface Action<D> {

  void execute(final D runtimeObject) throws Exception;

}
