package org.camunda.bpm.scenarios;

import org.camunda.bpm.engine.runtime.ProcessInstance;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ScenarioStarter {

  ProcessInstance start();

}
