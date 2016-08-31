package org.camunda.bpm.scenario.impl.waitstate;


import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.ScenarioAction;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;
import org.camunda.bpm.scenario.impl.delegate.AbstractProcessInstanceDelegate;
import org.camunda.bpm.scenario.impl.util.Time;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class TimerIntermediateEventWaitstate extends AbstractProcessInstanceDelegate {

  public TimerIntermediateEventWaitstate(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner, instance);
  }

  @Override
  protected ProcessInstance getDelegate() {
    return getRuntimeService().createProcessInstanceQuery().processInstanceId(getProcessInstance().getId()).singleResult();
  }

  @Override
  protected ScenarioAction action(Scenario.Process scenario) {
    return scenario.actsOnTimerIntermediateEvent(getActivityId());
  }

  public void execute() {
    ScenarioAction action = action();
    Time.set(isExecutableAt());
    try {
      if (action != null)
        action.execute(this);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    runner.setExecuted(this);
  }

}
