package org.camunda.bpm.scenarios;


import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ReceiveTaskWaitstate extends MessageEventWaitstate {

  public ReceiveTaskWaitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine, instance);
  }

  protected static String getActivityType() {
    return "receiveTask";
  }

  @Override
  protected void execute(Scenario scenario) {
    scenario.atReceiveTask(getActivityId()).execute(this);
  }

}
