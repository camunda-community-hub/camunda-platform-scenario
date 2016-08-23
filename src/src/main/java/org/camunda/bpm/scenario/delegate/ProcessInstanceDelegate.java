package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.runner.Waitstate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class ProcessInstanceDelegate extends Waitstate<ProcessInstance> implements ProcessInstance {

  public ProcessInstanceDelegate(ProcessEngine processEngine, HistoricActivityInstance instance, String duration) {
    super(processEngine, instance, duration);
  }

  public String getProcessDefinitionId() {
    return runtimeDelegate.getProcessDefinitionId();
  }

  public String getBusinessKey() {
    return runtimeDelegate.getBusinessKey();
  }

  public String getCaseInstanceId() {
    return runtimeDelegate.getCaseInstanceId();
  }

  public boolean isSuspended() {
    return runtimeDelegate.isSuspended();
  }

  public String getId() {
    return runtimeDelegate.getId();
  }

  public boolean isEnded() {
    return runtimeDelegate.isEnded();
  }

  public String getProcessInstanceId() {
    return runtimeDelegate.getProcessInstanceId();
  }

  public String getTenantId() {
    return runtimeDelegate.getTenantId();
  }

}
