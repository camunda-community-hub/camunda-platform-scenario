package org.camunda.bpm.scenario.impl.delegate;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.delegate.ProcessInstanceDelegate;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ProcessInstanceDelegateImpl extends AbstractDelegate<ProcessInstance> implements ProcessInstanceDelegate {

  ProcessRunnerImpl runner;

  protected ProcessInstanceDelegateImpl(ProcessRunnerImpl runner, ProcessInstance processInstance) {
    super(processInstance);
    this.runner = runner;
  }

  public static ProcessInstanceDelegate newInstance(ProcessRunnerImpl runner, ProcessInstance processInstance) {
    return processInstance != null ? new ProcessInstanceDelegateImpl(runner, processInstance) : null;
  }

  public String getProcessDefinitionId() {
    return delegate.getProcessDefinitionId();
  }

  public String getBusinessKey() {
    return delegate.getBusinessKey();
  }

  public String getCaseInstanceId() {
    return delegate.getCaseInstanceId();
  }

  public boolean isSuspended() {
    return delegate.isSuspended();
  }

  public String getId() {
    return delegate.getId();
  }

  public boolean isEnded() {
    return delegate.isEnded();
  }

  public String getProcessInstanceId() {
    return delegate.getProcessInstanceId();
  }

  public String getTenantId() {
    return delegate.getTenantId();
  }

}
