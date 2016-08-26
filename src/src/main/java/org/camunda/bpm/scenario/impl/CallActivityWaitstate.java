package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.CallActivityAction;
import org.camunda.bpm.scenario.action.ScenarioAction;
import org.camunda.bpm.scenario.delegate.ProcessInstanceDelegate;
import org.camunda.bpm.scenario.impl.delegate.ProcessInstanceDelegateImpl;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class CallActivityWaitstate extends ProcessInstanceDelegateImpl {

  public CallActivityWaitstate(ProcessRunnerImpl runner, HistoricActivityInstance instance, String duration) {
    super(runner, instance, duration);
  }

  @Override
  protected ProcessInstance getRuntimeDelegate() {
    return getRuntimeService().createProcessInstanceQuery().processInstanceId(historicDelegate.getCalledProcessInstanceId()).singleResult();
  }

  @Override
  protected ScenarioAction<ProcessInstanceDelegate> action(final Scenario.Process scenario) {
    final ProcessRunnerImpl runner = (ProcessRunnerImpl) scenario.actsOnCallActivity(getActivityId());
    if (runner != null) {
      return new CallActivityAction() {
        @Override
        public void execute(ProcessInstanceDelegate processInstance) {
          runner.running((CallActivityWaitstate) processInstance);
        }
      };
    }
    return null;
  }

  protected void leave() {
    throw new UnsupportedOperationException();
  }

  protected void leave(Map<String, Object> variables) {
    throw new UnsupportedOperationException();
  }

}
