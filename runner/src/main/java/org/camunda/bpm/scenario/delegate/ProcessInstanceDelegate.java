package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.defer.Deferrable;

/**
 * @author <a href="martin.schimak@plexiti.com">Martin Schimak</a>
 */
public interface ProcessInstanceDelegate extends ProcessInstance, Deferrable {

}
