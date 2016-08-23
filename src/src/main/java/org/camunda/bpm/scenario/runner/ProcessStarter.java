package org.camunda.bpm.scenario.runner;

import org.camunda.bpm.engine.runtime.ProcessInstance;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ProcessStarter {

  ProcessInstance start();

}
