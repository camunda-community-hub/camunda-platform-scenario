package org.camunda.bpm.scenarios;


import org.camunda.bpm.engine.ProcessEngine;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ExternalTaskWaitstate extends Waitstate {

  public ExternalTaskWaitstate(ProcessEngine processEngine) {
    super(processEngine);
  }

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
