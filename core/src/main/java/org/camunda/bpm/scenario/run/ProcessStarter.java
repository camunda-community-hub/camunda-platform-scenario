package org.camunda.bpm.scenario.run;

import org.camunda.bpm.engine.runtime.ProcessInstance;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ProcessStarter {

  /**
   * Method to be implemented with custom code to start a
   * process instance.
   *
   * @return a new process instance started by custom code
   */
  ProcessInstance start();

}
