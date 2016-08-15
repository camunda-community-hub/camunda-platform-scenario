package org.camunda.bpm.scenarios;


import org.camunda.bpm.engine.ProcessEngine;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class EventBasedGatewayWaitstate extends Waitstate<EventBasedGatewayWaitstate.EventBasedGateway> {

  protected EventBasedGatewayWaitstate(ProcessEngine processEngine, String executionId) {
    super(processEngine, executionId);
  }

  protected class EventBasedGateway {
  }

  @Override
  protected EventBasedGateway get() {
    return new EventBasedGateway();
  }

  protected void leave() {
    throw new UnsupportedOperationException();
  };

  protected void leave(Map<String, Object> variables) {
    throw new UnsupportedOperationException();
  };

}
