package org.camunda.bpm.scenario.impl.waitstate;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.act.Action;
import org.camunda.bpm.scenario.delegate.ProcessInstanceDelegate;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;
import org.camunda.bpm.scenario.impl.delegate.AbstractProcessInstanceDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class CallActivityExecutable extends AbstractProcessInstanceDelegate {

  public CallActivityExecutable(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner, instance);
  }

  @Override
  protected ProcessInstance getDelegate() {
    return getRuntimeService().createProcessInstanceQuery().processInstanceId(historicDelegate.getCalledProcessInstanceId()).singleResult();
  }

  @Override
  protected Action<ProcessInstanceDelegate> action(final ProcessScenario scenario) {
    final ProcessRunnerImpl runner = (ProcessRunnerImpl) scenario.runsCallActivity(getActivityId());
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

}
