package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.act.Action;
import org.camunda.bpm.scenario.defer.Deferred;
import org.camunda.bpm.scenario.delegate.ProcessInstanceDelegate;
import org.camunda.bpm.scenario.impl.delegate.ProcessInstanceDelegateImpl;
import org.camunda.bpm.scenario.impl.util.Log;
import org.camunda.bpm.scenario.impl.util.Time;

import java.util.Date;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class WaitstateExecutable<I> extends AbstractExecutable<I> {

  protected HistoricActivityInstance historicDelegate;

  protected WaitstateExecutable(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
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

  @SuppressWarnings("unchecked")
  public void execute() {
    Action action = action();
    if (action == null)
      throw new AssertionError("Process Instance {"
          + getProcessInstance().getProcessDefinitionId() + ", "
          + getProcessInstance().getProcessInstanceId() + "} "
          + "waits at an unexpected " + getClass().getSimpleName().substring(0, getClass().getSimpleName().length() - 10)
          + " '" + historicDelegate.getActivityId() +"'.");
    Time.set(isExecutableAt());
    try {
      Log.Action.ActingOn.log(
          historicDelegate.getActivityType(),
          historicDelegate.getActivityName(),
          historicDelegate.getActivityId(),
          runner.getProcessDefinitionKey(),
          historicDelegate.getProcessInstanceId(),
          null,
          null
      );
      action.execute(this);
    } catch (Exception e) {
      throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
    }
    runner.setExecuted(this);
  }

  protected abstract Action action(ProcessScenario scenario);

  protected final Action action() {
    return action(runner.scenario);
  };

  public Date isExecutableAt() {
    return historicDelegate.getStartTime();
  }

  public void defer(String period, Deferred action) {
    Deferreds.newInstance(runner, historicDelegate, period, action);
  }

  @Override
  @SuppressWarnings("unchecked")
  public int compareTo(AbstractExecutable other) {
    int compare = super.compareTo(other);
    return compare == 0 ? idComparator.compare(historicDelegate.getId(), ((WaitstateExecutable) other).historicDelegate.getId()) : compare;
  }

}
