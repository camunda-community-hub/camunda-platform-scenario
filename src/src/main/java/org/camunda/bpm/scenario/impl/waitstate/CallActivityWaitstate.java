package org.camunda.bpm.scenario.impl.waitstate;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.CallActivityAction;
import org.camunda.bpm.scenario.action.ScenarioAction;
import org.camunda.bpm.scenario.delegate.ProcessInstanceDelegate;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;
import org.camunda.bpm.scenario.impl.delegate.AbstractProcessInstanceDelegate;

import java.util.Date;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class CallActivityWaitstate extends AbstractProcessInstanceDelegate {

  public CallActivityWaitstate(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner, instance);
  }

  @Override
  protected ProcessInstance getDelegate() {
    return getRuntimeService().createProcessInstanceQuery().processInstanceId(historicDelegate.getCalledProcessInstanceId()).singleResult();
  }

  @Override
  protected ScenarioAction<ProcessInstanceDelegate> action(final Scenario.Process scenario) {
    final ProcessRunnerImpl runner = (ProcessRunnerImpl) scenario.runsCallActivity(getActivityId());
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

}
