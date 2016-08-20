package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class ProcessEngineServicesDelegate implements ProcessEngineServices {

  protected ProcessEngine processEngine;

  public ProcessEngineServicesDelegate(ProcessEngine processEngine) {
    this.processEngine = processEngine;
  }

  public ProcessEngine getProcessEngine() {
    return processEngine;
  }

  @Override
  public RuntimeService getRuntimeService() {
    return processEngine.getRuntimeService();
  }

  @Override
  public RepositoryService getRepositoryService() {
    return processEngine.getRepositoryService();
  }

  @Override
  public FormService getFormService() {
    return processEngine.getFormService();
  }

  @Override
  public TaskService getTaskService() {
    return processEngine.getTaskService();
  }

  @Override
  public HistoryService getHistoryService() {
    return processEngine.getHistoryService();
  }

  @Override
  public IdentityService getIdentityService() {
    return processEngine.getIdentityService();
  }

  @Override
  public ManagementService getManagementService() {
    return processEngine.getManagementService();
  }

  @Override
  public AuthorizationService getAuthorizationService() {
    return processEngine.getAuthorizationService();
  }

  @Override
  public CaseService getCaseService() {
    return processEngine.getCaseService();
  }

  @Override
  public FilterService getFilterService() {
    return processEngine.getFilterService();
  }

  @Override
  public ExternalTaskService getExternalTaskService() {
    return processEngine.getExternalTaskService();
  }

  @Override
  public DecisionService getDecisionService() {
    return processEngine.getDecisionService();
  }

}
