package org.camunda.bpm.scenario.act;


/**
 * @author Martin Schimak
 */
public interface Action<D> {

  void execute(final D runtimeObject) throws Exception;

}
