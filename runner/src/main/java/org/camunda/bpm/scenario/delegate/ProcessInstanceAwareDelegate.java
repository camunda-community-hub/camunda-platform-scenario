package org.camunda.bpm.scenario.delegate;

/**
 * @author Martin Schimak
 */
public interface ProcessInstanceAwareDelegate {

  /**
   * Get the process instance this object is associated to.
   *
   * @return process instance this object is associated to
   */
  ProcessInstanceDelegate getProcessInstance();

}
