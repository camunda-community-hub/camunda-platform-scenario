package org.camunda.bpm.scenarios;

import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class Savepoint<O> {

  protected ProcessEngine processEngine;
  protected String executionId;

  protected Savepoint(ProcessEngine processEngine, String executionId) {
    this.processEngine = processEngine;
    this.executionId = executionId;
  }

  protected abstract O get();

  protected abstract void leave();

  public RuntimeService getRuntimeService() {
    return processEngine.getRuntimeService();
  }

  public RepositoryService getRepositoryService() {
    return processEngine.getRepositoryService();
  }

  public ProcessEngine getProcessEngine() {
    return processEngine;
  }

  public FormService getFormService() {
    return processEngine.getFormService();
  }

  public TaskService getTaskService() {
    return processEngine.getTaskService();
  }

  public HistoryService getHistoryService() {
    return processEngine.getHistoryService();
  }

  public IdentityService getIdentityService() {
    return processEngine.getIdentityService();
  }

  public ManagementService getManagementService() {
    return processEngine.getManagementService();
  }

  public AuthorizationService getAuthorizationService() {
    return processEngine.getAuthorizationService();
  }

  public CaseService getCaseService() {
    return processEngine.getCaseService();
  }

  public FilterService getFilterService() {
    return processEngine.getFilterService();
  }

  public ExternalTaskService getExternalTaskService() {
    return processEngine.getExternalTaskService();
  }

  public DecisionService getDecisionService() {
    return processEngine.getDecisionService();
  }

  public ProcessInstance getProcessInstance() {
    Execution execution = getRuntimeService().createExecutionQuery().executionId(executionId).singleResult();
    return getRuntimeService().createProcessInstanceQuery().processInstanceId(execution.getProcessInstanceId()).singleResult();
  };

}
