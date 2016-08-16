package org.camunda.bpm.scenarios;


import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.EventSubscription;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ExternalTaskWaitstate extends Waitstate<ExternalTask> {

  private static final String WORKER_ID = "workerId";

  public ExternalTaskWaitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine, instance);
  }

  @Override
  protected ExternalTask get() {
    return getExternalTaskService().createExternalTaskQuery().executionId(getExecutionId()).singleResult();
  }

  protected static List<String> getActivityTypes() {
    return Arrays.asList("serviceTask", "sendTask", "intermediateMessageThrow");
  }

  @Override
  protected void execute(Scenario scenario) {
    scenario.atExternalTask(getActivityId()).execute(this);
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

  public ExternalTask getExternalTask() {
    return get();
  }

}
