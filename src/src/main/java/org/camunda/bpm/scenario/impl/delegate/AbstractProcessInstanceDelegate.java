package org.camunda.bpm.scenario.impl.delegate;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.delegate.ProcessInstanceDelegate;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;
import org.camunda.bpm.scenario.impl.WaitstateExecutable;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class AbstractProcessInstanceDelegate extends WaitstateExecutable<ProcessInstance> implements ProcessInstanceDelegate {

  public AbstractProcessInstanceDelegate(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner, instance);
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
