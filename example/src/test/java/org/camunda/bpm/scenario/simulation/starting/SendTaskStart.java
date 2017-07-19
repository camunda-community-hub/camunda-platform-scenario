package org.camunda.bpm.scenario.simulation.starting;

import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
@FunctionalInterface
public interface SendTaskStart {

    String deferal(final ExternalTaskDelegate externalTask);

}
