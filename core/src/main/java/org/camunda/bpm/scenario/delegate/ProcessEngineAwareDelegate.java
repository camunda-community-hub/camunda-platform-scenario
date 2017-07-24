package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.ProcessEngine;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ProcessEngineAwareDelegate {

  /**
   * Get the process engine this object is associated to.
   *
   * @return process instance this object is associated to
   */
  ProcessEngine getProcessEngine();

}
