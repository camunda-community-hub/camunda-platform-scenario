package org.camunda.bpm.scenarios;

import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

public abstract class Waitstate<O> {

  protected ProcessEngine processEngine;
  protected String executionId;

  protected Waitstate(ProcessEngine processEngine, String executionId) {
    this.processEngine = processEngine;
    this.executionId = executionId;
  }

  protected abstract O get();

  protected abstract void leave();

  protected abstract void leave(Map<String, Object> variables);

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
    throw new NotImplementedException();
  };

  public void receiveSignal(String signalName) {
    throw new NotImplementedException();
  };

  public void receiveSignal(String signalName, Map<String, Object> variables) {
    throw new NotImplementedException();
  };

  public void receiveMessage(String messageName) {
    throw new NotImplementedException();
  };

  public void receiveMessage(String messageName, Map<String, Object> variables) {
    throw new NotImplementedException();
  };

  public void triggerTimer(String activityId) {
    throw new NotImplementedException();
  };

}
