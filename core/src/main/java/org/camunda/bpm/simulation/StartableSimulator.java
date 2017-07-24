package org.camunda.bpm.simulation;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface StartableSimulator {

    /**
     * Start the new process instance by providing a process definition key.
     *
     * @param processDefinitionKey to be used to start a process instance
     */
    ExecutableSimulator startByKey(String processDefinitionKey);

}
