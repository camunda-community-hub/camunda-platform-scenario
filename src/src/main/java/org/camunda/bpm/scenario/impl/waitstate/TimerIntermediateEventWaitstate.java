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
    if (duration != null) {
      throw new IllegalStateException("Found a duration '" + duration + "' set. " +
          "Explicit durations are not supported for '" + getClass().getSimpleName()
          + "'. Its duration always depends on the timers defined in the BPMN process.");
    }
  }

  @Override
  public void execute() {
    ScenarioAction action = action(runner.getScenario());
    if (action == null)
      throw new AssertionError("Process Instance {"
          + getProcessInstance().getProcessDefinitionId() + ", "
          + getProcessInstance().getProcessInstanceId() + "} "
          + "waits at an unexpected " + getClass().getSimpleName().substring(0, getClass().getSimpleName().length() - 9)
          + " '" + historicDelegate.getActivityId() +"'.");
    action.execute(this);
    Job job = getManagementService().createJobQuery().timers().jobId(getId()).singleResult();
    if (job != null)
      getManagementService().executeJob(job.getId());
    runner.setExecutedHistoricActivityIds(historicDelegate);
  }

  @Override
  protected Job getRuntimeDelegate() {
    return getManagementService().createJobQuery().timers().executionId(getExecutionId()).singleResult();
  }

  @Override
  protected ScenarioAction action(Scenario.Process scenario) {
    return scenario.actsOnTimerIntermediateEvent(getActivityId());
  }

  protected void leave() {
    getManagementService().executeJob(getRuntimeDelegate().getId());
  }

  protected void leave(Map<String, Object> variables) {
    getRuntimeService().setVariables(getProcessInstance().getId(), variables);
    leave();
  }

  @Override
  public Date isExecutableAt() {
    return getDuedate();
  }

}
