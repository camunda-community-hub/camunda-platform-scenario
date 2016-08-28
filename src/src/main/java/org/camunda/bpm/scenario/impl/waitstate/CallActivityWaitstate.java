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

  @Override
  public Date isExecutableAt() {
    String duration = runner.getDuration(historicDelegate);
    if (runner.getDuration(historicDelegate) != null)
      throw new IllegalStateException(String.format("The explicit duration '%s' defined to wait for action " +
          "on activity '%s' is not supported for call activities. Their overall execution duration always " +
          "depends on the the called process instance's duration.", duration, getActivityId()));
    return super.isExecutableAt();
  }

}
