package org.camunda.bpm.scenario;

import org.camunda.bpm.engine.runtime.ProcessInstance;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ExecutedScenario {

  ProcessInstance getProcessInstance();

}
