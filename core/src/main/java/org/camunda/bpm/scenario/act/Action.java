package org.camunda.bpm.scenario.act;


/**
 * @author <a href="martin.schimak@plexiti.com">Martin Schimak</a>
 */
public interface Action<D> {

  void execute(final D runtimeObject) throws Exception;

}
