package org.camunda.bpm.scenario.runner;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.CallActivityAction;
import org.camunda.bpm.scenario.action.ScenarioAction;
import org.camunda.bpm.scenario.delegate.ProcessInstanceDelegate;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class CallActivityWaitstate extends ProcessInstanceDelegate {

  public CallActivityWaitstate(ScenarioRunnerImpl runner, HistoricActivityInstance instance, String duration) {
    super(runner, instance, duration);
  }

  @Override
  protected ProcessInstance getRuntimeDelegate() {
    return getRuntimeService().createProcessInstanceQuery().processInstanceId(historicDelegate.getCalledProcessInstanceId()).singleResult();
  }

  @Override
  protected ScenarioAction<CallActivityWaitstate> action(final Scenario.Process scenario) {
    return new CallActivityAction() {
      @Override
      public void execute(CallActivityWaitstate runtimeInstance) {
        ScenarioRunnerImpl runner = (ScenarioRunnerImpl) scenario.atCallActivity(getActivityId());
        runner.running(runtimeInstance);
      }
    };
  }

  protected void leave() {
    throw new UnsupportedOperationException();
  }

  protected void leave(Map<String, Object> variables) {
    throw new UnsupportedOperationException();
  }

}
