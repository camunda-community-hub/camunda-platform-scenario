package org.camunda.bpm.scenarios;


import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.EventSubscription;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ReceiveTaskWaitstate extends MessageEventWaitstate {

  protected ReceiveTaskWaitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine, instance);
  }

  protected static String getActivityType() {
    return "receiveTask";
  }

  @Override
  protected void execute(Scenario scenario) {
    scenario.atReceiveTask(getActivityId()).execute(this);
  }

  @Override
  public void receiveMessage() {
    super.receiveMessage();
  }

  @Override
  public void receiveMessage(Map<String, Object> variables) {
    super.receiveMessage(variables);
  }

  @Override
  public EventSubscription getEventSubscription() {
    return super.getEventSubscription();
  }

}
