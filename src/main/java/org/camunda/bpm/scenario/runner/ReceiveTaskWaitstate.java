package org.camunda.bpm.scenario.runner;


import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.ScenarioAction;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ReceiveTaskWaitstate extends MessageIntermediateCatchEventWaitstate {

  protected ReceiveTaskWaitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine, instance);
  }

  @Override
  protected ScenarioAction action(Scenario scenario) {
    return scenario.atReceiveTask(getActivityId());
  }

  @Override
  public void receiveMessage() {
    super.receiveMessage();
  }

  @Override
  public void receiveMessage(Map<String, Object> variables) {
    super.receiveMessage(variables);
  }

}
