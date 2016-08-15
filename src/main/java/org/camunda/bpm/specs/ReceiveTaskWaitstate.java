package org.camunda.bpm.specs;


import org.camunda.bpm.engine.ProcessEngine;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ReceiveTaskWaitstate extends MessageEventWaitstate {

  public ReceiveTaskWaitstate(ProcessEngine processEngine) {
    super(processEngine);
  }

}
