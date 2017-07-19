package org.camunda.bpm.scenario.simulation.finishing;

import org.camunda.bpm.scenario.delegate.TaskDelegate;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
@FunctionalInterface
public interface UserTaskFinish {

    Map<String, Object> variables(final TaskDelegate task);

}
