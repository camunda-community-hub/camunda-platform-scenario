package org.camunda.bpm.specs;


import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class EventBasedGatewayWaitstate extends Waitstate {

  protected void leave() {
    throw new UnsupportedOperationException();
  };

  protected void leave(Map<String, Object> variables) {
    throw new UnsupportedOperationException();
  };

}
