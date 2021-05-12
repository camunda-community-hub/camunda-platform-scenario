package org.camunda.bpm.scenario.impl.waitstate;


import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.act.Action;
import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;

import java.util.Map;

/**
 * @author <a href="martin.schimak@plexiti.com">Martin Schimak</a>
 */
public class MessageEndEventExecutable extends MessageIntermediateThrowEventExecutable {

  public MessageEndEventExecutable(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner, instance);
  }

  @Override
  protected Action<ExternalTaskDelegate> action(ProcessScenario scenario) {
    return scenario.waitsAtMessageEndEvent(getActivityId());
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
  public void handleBpmnError(String errorCode, Map<String, Object> variables) {
    super.handleBpmnError(errorCode, variables);
  }

}
