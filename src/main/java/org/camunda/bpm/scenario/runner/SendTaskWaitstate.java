package org.camunda.bpm.scenario.runner;


import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.ScenarioAction;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class SendTaskWaitstate extends ServiceTaskWaitstate {

  protected SendTaskWaitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine, instance);
  }

  @Override
  protected ScenarioAction action(Scenario scenario) {
    return scenario.atSendTask(getActivityId());
  }

  @Override
  public void complete() {
    super.complete();
  }

  @Override
  public void complete(Map<String, Object> variables) {
    super.complete(variables);
  }

  @Override
  public void handleBpmnError(String errorCode) {
    super.handleBpmnError(errorCode);
  }

  @Override
  public void handleFailure(String errorMessage, int retries, long retryTimeout) {
    super.handleFailure(errorMessage, retries, retryTimeout);
  }

}
