package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.scenario.defer.Deferred;
import org.camunda.bpm.scenario.impl.util.Log;
import org.camunda.bpm.scenario.impl.util.Time;

import java.util.Date;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class DeferredExecutable extends AbstractExecutable<HistoricActivityInstance> {

  private static int sequence;
  private Integer id = ++sequence;
  private Date isExecutableAt;
  private Deferred action;

  protected DeferredExecutable(ProcessRunnerImpl runner, HistoricActivityInstance instance, String period, Deferred action) {
    super(runner);
    this.delegate = instance;
    this.isExecutableAt = Time.dateAfter(period);
    this.action = action;
    Log.Action.Deferring_Action.log(
        instance.getActivityType(),
        instance.getActivityName(),
        instance.getActivityId(),
        runner.getProcessDefinitionKey(),
        instance.getProcessInstanceId(),
        action.toString(),
        isExecutableAt
    );
    Deferreds.add(this);
  }

  @Override
  public String getExecutionId() {
    return delegate.getExecutionId();
  }

  @Override
  protected HistoricActivityInstance getDelegate() {
    return getHistoryService().createHistoricActivityInstanceQuery().activityInstanceId(delegate.getId()).unfinished().singleResult();
  }

  @Override
  protected Date isExecutableAt() {
    return isExecutableAt;
  }

  @Override
  public void execute() {
    if (getDelegate() != null) {
      Time.set(isExecutableAt());
      try {
        Log.Action.Executing_Action.log(
            delegate.getActivityType(),
            delegate.getActivityName(),
            delegate.getActivityId(),
            runner.getProcessDefinitionKey(),
            delegate.getProcessInstanceId(),
            action.toString(),
            isExecutableAt
        );
        action.execute();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    Deferreds.remove(this);
  }

  @Override
  public int compareTo(AbstractExecutable other) {
    int compare = super.compareTo(other);
    return compare == 0 ? id.compareTo(((DeferredExecutable) other).id) : compare;
  }

}
