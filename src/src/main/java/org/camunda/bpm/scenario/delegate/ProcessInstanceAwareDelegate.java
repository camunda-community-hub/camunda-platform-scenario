package org.camunda.bpm.scenario.delegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ProcessInstanceAwareDelegate {

  ProcessInstanceDelegate getProcessInstance();

}
