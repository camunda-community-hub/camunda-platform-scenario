package org.camunda.bpm.specs;


import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ExternalTaskWaitstate extends Waitstate {

  protected void leave() {
    throw new NotImplementedException();
  };

  protected void leave(Map<String, Object> variables) {
    throw new NotImplementedException();
  };

  public void completeExternalTask() {
    leave();
  }

  public void completeExternalTask(Map<String, Object> variables) {
    leave(variables);
  }

}
