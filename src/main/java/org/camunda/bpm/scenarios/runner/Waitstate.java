package org.camunda.bpm.scenarios.runner;

import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.*;
import org.camunda.bpm.scenarios.Scenario;
import org.camunda.bpm.scenarios.waitstate.Savepoint;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class Waitstate<I> extends Savepoint<I> {

  protected HistoricActivityInstance historicActivityInstance;

  protected Waitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine);
    this.historicActivityInstance = instance;
    this.delegate = get();
  }

  @Override
  public String getExecutionId() {
    return historicActivityInstance.getExecutionId();
  }

  public String getActivityId() {
    return historicActivityInstance.getActivityId();
  }

  protected abstract void execute(Scenario scenario);

  protected abstract void leave(Map<String, Object> variables);

  protected boolean unfinished() {
    return getHistoryService().createHistoricActivityInstanceQuery().activityInstanceId(historicActivityInstance.getId()).unfinished().singleResult() != null;
  }

  public SignalEventReceivedBuilder createSignal(String signalName) {
    return getRuntimeService().createSignalEvent(signalName);
  }

  public MessageCorrelationBuilder createMessage(String messageName) {
    return getRuntimeService().createMessageCorrelation(messageName);
  }

  public void triggerTimer(String activityId) {
    Job job = getManagementService().createJobQuery().processInstanceId(getProcessInstance().getId()).activityId(activityId).timers().singleResult();
    getManagementService().executeJob(job.getId());
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof Waitstate && getActivityId().equals(((Waitstate) o).getActivityId());
  }

}
