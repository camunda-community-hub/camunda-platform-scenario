package org.camunda.bpm.scenario.impl.delegate;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.delegate.ProcessInstanceDelegate;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;
import org.camunda.bpm.scenario.impl.AbstractWaitstate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class AbstractProcessInstanceDelegate extends AbstractWaitstate<ProcessInstance> implements ProcessInstanceDelegate {

  public AbstractProcessInstanceDelegate(ProcessRunnerImpl runner, HistoricActivityInstance instance, String duration) {
    super(runner, instance, duration);
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
