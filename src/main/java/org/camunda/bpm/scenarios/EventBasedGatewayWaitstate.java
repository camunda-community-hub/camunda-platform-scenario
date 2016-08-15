package org.camunda.bpm.scenarios;


import org.camunda.bpm.engine.ProcessEngine;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class EventBasedGatewayWaitstate extends Waitstate {

  protected EventBasedGatewayWaitstate(ProcessEngine processEngine) {
    super(processEngine);
  }

  protected void leave() {
    throw new UnsupportedOperationException();
  };

  protected void leave(Map<String, Object> variables) {
    throw new UnsupportedOperationException();
  };

}
