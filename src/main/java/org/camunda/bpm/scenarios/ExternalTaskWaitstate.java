package org.camunda.bpm.scenarios;


import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.externaltask.ExternalTask;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ExternalTaskWaitstate extends Waitstate<ExternalTask> {

  private static final String WORKER_ID = "workedId";

  public ExternalTaskWaitstate(ProcessEngine processEngine, String executionId, String activityId) {
    super(processEngine, executionId, activityId);
  }

  @Override
  protected ExternalTask get() {
    return getExternalTaskService().createExternalTaskQuery().executionId(executionId).singleResult();
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
    getExternalTaskService().fetchAndLock(Integer.MAX_VALUE, WORKER_ID);
  }

  public void completeExternalTask() {
    leave();
  }

  public void completeExternalTask(Map<String, Object> variables) {
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
