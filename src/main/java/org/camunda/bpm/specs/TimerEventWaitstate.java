package org.camunda.bpm.specs;


import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class TimerEventWaitstate extends Waitstate {

  protected void leave() {
    throw new NotImplementedException();
  };

  protected void leave(Map<String, Object> variables) {
    throw new NotImplementedException();
  };

  public void triggerTimer() {
    leave();
  }

  public void triggerTimer(Map<String, Object> variables) {
    leave(variables);
  }

}
