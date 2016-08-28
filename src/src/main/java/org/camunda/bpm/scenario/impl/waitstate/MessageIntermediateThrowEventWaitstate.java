package org.camunda.bpm.scenario.impl.waitstate;


import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.ScenarioAction;
import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class MessageIntermediateThrowEventWaitstate extends ServiceTaskWaitstate {

  public MessageIntermediateThrowEventWaitstate(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner, instance);
  }

  @Override
  protected ScenarioAction<ExternalTaskDelegate> action(Scenario.Process scenario) {
    return scenario.actsOnMessageIntermediateThrowEvent(getActivityId());
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
