package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.AuthorizationService;
import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.DecisionService;
import org.camunda.bpm.engine.ExternalTaskService;
import org.camunda.bpm.engine.FilterService;
import org.camunda.bpm.engine.FormService;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngineServices;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;

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
