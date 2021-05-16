package org.camunda.bpm.scenario.impl.delegate;

import org.camunda.bpm.engine.*;

/**
 * @author Martin Schimak
 */
public abstract class AbstractProcessEngineServicesDelegate {

  protected ProcessEngine processEngine;

  public AbstractProcessEngineServicesDelegate(ProcessEngine processEngine) {
    this.processEngine = processEngine;
  }

  public ProcessEngine getProcessEngine() {
    return processEngine;
  }

  public RuntimeService getRuntimeService() {
    return processEngine.getRuntimeService();
  }

  public RepositoryService getRepositoryService() {
    return processEngine.getRepositoryService();
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

}
