package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.DeferredAction;
import org.camunda.bpm.scenario.action.ScenarioAction;
import org.camunda.bpm.scenario.delegate.ProcessInstanceDelegate;
import org.camunda.bpm.scenario.impl.delegate.ProcessInstanceDelegateImpl;
import org.camunda.bpm.scenario.impl.util.Time;

import java.util.Date;

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

  public ProcessInstanceDelegate getProcessInstance() {
    return ProcessInstanceDelegateImpl.newInstance(this, runner.processInstance);
  };

  @Override
  public String getExecutionId() {
    return historicDelegate.getExecutionId();
  }

  public String getActivityId() {
    return historicDelegate.getActivityId();
  }

  public void execute() {
    ScenarioAction action = action();
    if (action == null)
      throw new AssertionError("Process Instance {"
          + getProcessInstance().getProcessDefinitionId() + ", "
          + getProcessInstance().getProcessInstanceId() + "} "
          + "waits at an unexpected " + getClass().getSimpleName().substring(0, getClass().getSimpleName().length() - 9)
          + " '" + historicDelegate.getActivityId() +"'.");
    Time.set(isExecutableAt());
    try {
      action.execute(this);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    runner.setExecuted(this);
  }

  protected abstract ScenarioAction action(Scenario.Process scenario);

  protected final ScenarioAction action() {
    return action(runner.scenario);
  };

  public Date isExecutableAt() {
    return historicDelegate.getStartTime();
  }

  public void defer(String period, DeferredAction action) {
    Deferreds.newInstance(runner, historicDelegate, period, action);
  }

  @Override
  public int compareTo(AbstractExecutable other) {
    int compare = super.compareTo(other);
    return compare == 0 ? idComparator.compare(historicDelegate.getId(), ((ExecutableWaitstate) other).historicDelegate.getId()) : compare;
  }

}
