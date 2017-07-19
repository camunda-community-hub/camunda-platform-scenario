package org.camunda.bpm.scenario.simulation.finishing;

import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
@FunctionalInterface
public interface ServiceTaskFinish {

    Map<String, Object> variables(final ExternalTaskDelegate externalTask);

}
