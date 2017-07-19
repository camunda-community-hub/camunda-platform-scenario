package org.camunda.bpm.scenario.simulation.starting;

import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
@FunctionalInterface
public interface ServiceTaskStart {

    String deferal(final ExternalTaskDelegate externalTask);

}
