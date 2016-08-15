package org.camunda.bpm.scenarios;


import org.camunda.bpm.engine.ProcessEngine;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class SignalEventWaitstate extends Waitstate {

  public SignalEventWaitstate(ProcessEngine processEngine) {
    super(processEngine);
  }

  protected void leave() {
    throw new NotImplementedException();
  };

  protected void leave(Map<String, Object> variables) {
    throw new NotImplementedException();
  };

  public void receiveSignal() {
    leave();
  }

  public void receiveSignal(Map<String, Object> variables) {
    leave(variables);
  }

}
