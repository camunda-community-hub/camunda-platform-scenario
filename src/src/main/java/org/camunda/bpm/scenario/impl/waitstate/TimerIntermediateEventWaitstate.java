package org.camunda.bpm.scenario.impl.waitstate;


import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.ScenarioAction;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;
import org.camunda.bpm.scenario.impl.delegate.AbstractTimerJobDelegate;

import java.util.Date;
import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class TimerIntermediateEventWaitstate extends AbstractTimerJobDelegate {

  public TimerIntermediateEventWaitstate(ProcessRunnerImpl runner, HistoricActivityInstance instance, String duration) {
    super(runner, instance, duration);
  }

  @Override
  protected Job getDelegate() {
    return getManagementService().createJobQuery().timers().executionId(getExecutionId()).singleResult();
  }

  @Override
  protected ScenarioAction action(Scenario.Process scenario) {
    return scenario.actsOnTimerIntermediateEvent(getActivityId());
  }

  protected void leave() {
    getManagementService().executeJob(getDelegate().getId());
  }

  protected void leave(Map<String, Object> variables) {
    getRuntimeService().setVariables(getProcessInstance().getId(), variables);
    leave();
  }

}
