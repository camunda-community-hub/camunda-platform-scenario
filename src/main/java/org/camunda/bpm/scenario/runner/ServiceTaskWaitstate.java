package org.camunda.bpm.scenario.runner;


import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.ScenarioAction;
import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ServiceTaskWaitstate extends ExternalTaskDelegate {

  private static final String WORKER_ID = "workerId";

  protected ServiceTaskWaitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine, instance);
  }

  @Override
  protected ExternalTask getRuntimeDelegate() {
    return getExternalTaskService().createExternalTaskQuery().executionId(getExecutionId()).singleResult();
  }

  @Override
  protected ScenarioAction action(Scenario scenario) {
    return scenario.atServiceTask(getActivityId());
  }

  protected void leave() {
    fetchAndLock();
    getExternalTaskService().complete(getRuntimeDelegate().getId(), WORKER_ID);
  }

  protected void leave(Map<String, Object> variables) {
    fetchAndLock();
    getExternalTaskService().complete(getRuntimeDelegate().getId(), WORKER_ID, variables);
  }

  protected void fetchAndLock() {
    getExternalTaskService().fetchAndLock(Integer.MAX_VALUE, WORKER_ID).topic(getRuntimeDelegate().getTopicName(), Long.MAX_VALUE).execute();
  }

  public void complete() {
    leave();
  }

  public void complete(Map<String, Object> variables) {
    leave(variables);
  }

  public void handleBpmnError(String errorCode) {
    fetchAndLock();
    getExternalTaskService().handleBpmnError(getRuntimeDelegate().getId(), WORKER_ID, errorCode);
  }

  public void handleFailure(String errorMessage, int retries, long retryTimeout) {
    fetchAndLock();
    getExternalTaskService().handleFailure(getRuntimeDelegate().getId(), WORKER_ID, errorMessage, retries, retryTimeout);
  }

}
