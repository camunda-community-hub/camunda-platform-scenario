package org.camunda.bpm.scenario.impl.waitstate;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.act.Action;
import org.camunda.bpm.scenario.act.MockedCallActivityAction;
import org.camunda.bpm.scenario.delegate.ProcessInstanceDelegate;
import org.camunda.bpm.scenario.impl.MockedProcessInstanceRunner;
import org.camunda.bpm.scenario.impl.ProcessInstanceRunner;
import org.camunda.bpm.scenario.impl.delegate.AbstractProcessInstanceDelegate;

/**
 * @author Martin Schimak
 */
public class CallActivityExecutable extends AbstractProcessInstanceDelegate {

  public CallActivityExecutable(ProcessInstanceRunner runner, HistoricActivityInstance instance) {
    super(runner, instance);
  }

  @Override
  protected ProcessInstance getDelegate() {
    return getRuntimeService().createProcessInstanceQuery().processInstanceId(historicDelegate.getCalledProcessInstanceId()).singleResult();
  }

  @Override
  protected Action<ProcessInstanceDelegate> action(final ProcessScenario scenario) {
    final ProcessInstanceRunner mocked = (ProcessInstanceRunner) scenario.runsCallActivity(getActivityId());
    final MockedCallActivityAction action = scenario.waitsAtMockedCallActivity(getActivityId());
    final ProcessInstanceRunner runner = mocked != null ? mocked : (action != null ? new MockedProcessInstanceRunner(action) : null);
    if (runner != null) {
      return new Action<ProcessInstanceDelegate>() {
        @Override
        public void execute(ProcessInstanceDelegate processInstance) {
          runner.running((CallActivityExecutable) processInstance);
        }
      };
    }
    return null;
  }

  @Override
  public String getRootProcessInstanceId() {
    return getProcessInstance().getRootProcessInstanceId();
  }
}
