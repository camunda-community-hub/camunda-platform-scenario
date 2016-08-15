package org.camunda.bpm.specs;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

public abstract class Waitstate {

  protected ProcessEngine processEngine;

  protected Waitstate() {
    throw new NotImplementedException();
  }

  protected Waitstate(ProcessEngine processEngine) {
    this.processEngine = processEngine;
  }

  protected abstract void leave();

  protected abstract void leave(Map<String, Object> variables);

  public ProcessInstance getProcessInstance() {
    throw new NotImplementedException();
  };

  public void receiveSignal(String signalName) {
    throw new NotImplementedException();
  };

  public void receiveSignal(String signalName, Map<String, Object> variables) {
    throw new NotImplementedException();
  };

  public void receiveMessage(String messageName) {
    throw new NotImplementedException();
  };

  public void receiveMessage(String messageName, Map<String, Object> variables) {
    throw new NotImplementedException();
  };

  public void triggerTimer(String activityId) {
    throw new NotImplementedException();
  };

}
