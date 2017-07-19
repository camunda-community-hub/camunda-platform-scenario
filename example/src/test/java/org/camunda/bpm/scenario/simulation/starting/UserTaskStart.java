package org.camunda.bpm.scenario.simulation.starting;

import org.camunda.bpm.scenario.delegate.TaskDelegate;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
@FunctionalInterface
public interface UserTaskStart {

    String deferal(final TaskDelegate task);

}
