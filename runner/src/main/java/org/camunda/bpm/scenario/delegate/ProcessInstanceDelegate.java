package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.defer.Deferrable;

/**
 * @author Martin Schimak
 */
public interface ProcessInstanceDelegate extends ProcessInstance, Deferrable {

}
