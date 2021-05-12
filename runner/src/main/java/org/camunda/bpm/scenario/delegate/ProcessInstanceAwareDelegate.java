package org.camunda.bpm.scenario.delegate;

/**
 * @author <a href="martin.schimak@plexiti.com">Martin Schimak</a>
 */
public interface ProcessInstanceAwareDelegate {

  /**
   * Get the process instance this object is associated to.
   *
   * @return process instance this object is associated to
   */
  ProcessInstanceDelegate getProcessInstance();

}
