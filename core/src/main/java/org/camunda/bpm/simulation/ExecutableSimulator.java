package org.camunda.bpm.simulation;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ExecutableSimulator {

    /**
     * Execute the fully defined simulation run.
     *
     * @return an executed simulation
     */
    void execute(int times);

}
