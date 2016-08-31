package org.camunda.bpm.scenario.impl.waitstate;


import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.ScenarioAction;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;
import org.camunda.bpm.scenario.impl.delegate.AbstractExternalTaskDelegate;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ServiceTaskWaitstate extends AbstractExternalTaskDelegate {

  private static final String WORKER_ID = "workerId";

  public ServiceTaskWaitstate(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner, instance);
  }

  @Override
  protected ExternalTask getDelegate() {
    return getExternalTaskService().createExternalTaskQuery().executionId(getExecutionId()).singleResult();
  }

  @Override
  protected ScenarioAction action(Scenario.Process scenario) {
    return scenario.waitsAtServiceTask(getActivityId());
  }

  protected void fetchAndLock() {
    getExternalTaskService().fetchAndLock(Integer.MAX_VALUE, WORKER_ID).topic(getDelegate().getTopicName(), Long.MAX_VALUE).execute();
  }

  @Override
  public void complete() {
    fetchAndLock();
    getExternalTaskService().complete(getDelegate().getId(), WORKER_ID);
  }

  @Override
  public void complete(Map<String, Object> variables) {
    fetchAndLock();
    getExternalTaskService().complete(getDelegate().getId(), WORKER_ID, variables);
  }

  @Override
  public void handleBpmnError(String errorCode) {
    fetchAndLock();
    getExternalTaskService().handleBpmnError(getDelegate().getId(), WORKER_ID, errorCode);
  }

}
