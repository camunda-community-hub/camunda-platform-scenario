package org.camunda.bpm.scenario.impl.delegate;

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
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
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
