package org.camunda.bpm.scenario.impl.waitstate;


import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.act.Action;
import org.camunda.bpm.scenario.impl.ProcessInstanceRunner;
import org.camunda.bpm.scenario.impl.delegate.AbstractExternalTaskDelegate;

import java.util.Map;

/**
 * @author Martin Schimak
 */
public class ServiceTaskExecutable extends AbstractExternalTaskDelegate {

  private static final String WORKER_ID = "workerId";

  public ServiceTaskExecutable(ProcessInstanceRunner runner, HistoricActivityInstance instance) {
    super(runner, instance);
  }

  @Override
  protected ExternalTask getDelegate() {
    return getExternalTaskService().createExternalTaskQuery().executionId(getExecutionId()).singleResult();
  }

  @Override
  protected Action action(ProcessScenario scenario) {
    return scenario.waitsAtServiceTask(getActivityId());
  }

  protected void fetchAndLock() {
    getExternalTaskService().fetchAndLock(Integer.MAX_VALUE, WORKER_ID).topic(getDelegate().getTopicName(), Integer.MAX_VALUE).execute();
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

  @Override
  public void handleBpmnError(String errorCode, Map<String, Object> variables) {
    fetchAndLock();
    getExternalTaskService().handleBpmnError(getDelegate().getId(), WORKER_ID, errorCode, null, variables);
  }

  @Override
  public String getProcessDefinitionVersionTag() {
    return getRepositoryService().getProcessDefinition(getProcessDefinitionId()).getVersionTag();
  }

  @Override
  public String getBusinessKey() {
    return getProcessInstance().getBusinessKey();
  }
}
