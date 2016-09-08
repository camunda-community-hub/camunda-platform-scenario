package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.defer.Deferrable;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ProcessInstanceDelegate extends ProcessInstance, Deferrable {

}
