package org.camunda.bpm.scenarios.waitstate;


import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.scenarios.Scenario;
import org.camunda.bpm.scenarios.WaitstateAction;
import org.camunda.bpm.scenarios.delegate.ExternalTaskDelegate;

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
  protected ExternalTask get() {
    return getExternalTaskService().createExternalTaskQuery().executionId(getExecutionId()).singleResult();
  }

  @Override
  protected WaitstateAction action(Scenario scenario) {
    return scenario.atServiceTask(getActivityId());
  }

  protected void leave() {
    fetchAndLock();
    getExternalTaskService().complete(get().getId(), WORKER_ID);
  }

  protected void leave(Map<String, Object> variables) {
    fetchAndLock();
    getExternalTaskService().complete(get().getId(), WORKER_ID, variables);
  }

  protected void fetchAndLock() {
    getExternalTaskService().fetchAndLock(Integer.MAX_VALUE, WORKER_ID).topic(get().getTopicName(), Long.MAX_VALUE).execute();
  }

  public void complete() {
    leave();
  }

  public void complete(Map<String, Object> variables) {
    leave(variables);
  }

  public void handleBpmnError(String errorCode) {
    fetchAndLock();
    getExternalTaskService().handleBpmnError(get().getId(), WORKER_ID, errorCode);
  }

  public void handleFailure(String errorMessage, int retries, long retryTimeout) {
    fetchAndLock();
    getExternalTaskService().handleFailure(get().getId(), WORKER_ID, errorMessage, retries, retryTimeout);
  }

}
