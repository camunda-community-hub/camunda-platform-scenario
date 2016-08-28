package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.impl.calendar.DurationHelper;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;
import org.camunda.bpm.engine.runtime.SignalEventReceivedBuilder;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.ScenarioAction;

import java.util.Date;
import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class ExecutableWaitstate<I> extends AbstractExecutable<I> {

  protected HistoricActivityInstance historicDelegate;

  protected ExecutableWaitstate(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner);
    this.historicDelegate = instance;
    this.delegate = getDelegate();
  }

  @Override
  public String getExecutionId() {
    return historicDelegate.getExecutionId();
  }

  public String getActivityId() {
    return historicDelegate.getActivityId();
  }

  public void execute() {
    ScenarioAction action = action(runner.scenario);
    if (action == null)
      throw new AssertionError("Process Instance {"
          + getProcessInstance().getProcessDefinitionId() + ", "
          + getProcessInstance().getProcessInstanceId() + "} "
          + "waits at an unexpected " + getClass().getSimpleName().substring(0, getClass().getSimpleName().length() - 9)
          + " '" + historicDelegate.getActivityId() +"'.");
    ClockUtil.setCurrentTime(isExecutableAt());
    action.execute(this);
    runner.setExecuted(historicDelegate.getId());
  }

  protected abstract ScenarioAction action(Scenario.Process scenario);

  protected abstract void leave(Map<String, Object> variables);

  public Date isExecutableAt() {
    Date endTime = historicDelegate.getStartTime();
    String duration = runner.getDuration(historicDelegate);
    if (duration != null) {
      try {
        if (duration == null || !duration.startsWith("P")) {
          throw new IllegalArgumentException("Provided argument '" + duration + "' is not a duration expression.");
        }
        Date now = ClockUtil.getCurrentTime();
        ClockUtil.setCurrentTime(historicDelegate.getStartTime());
        DurationHelper durationHelper = new DurationHelper(duration);
        endTime = durationHelper.getDateAfter();
        ClockUtil.setCurrentTime(now);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return endTime;
  }

}
