package org.camunda.bpm.scenario.impl.delegate;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.defer.Deferred;
import org.camunda.bpm.scenario.delegate.ProcessInstanceDelegate;
import org.camunda.bpm.scenario.impl.WaitstateExecutable;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ProcessInstanceDelegateImpl extends AbstractDelegate<ProcessInstance> implements ProcessInstanceDelegate {

  WaitstateExecutable waitstate;

  protected ProcessInstanceDelegateImpl(WaitstateExecutable waitstate, ProcessInstance processInstance) {
    super(processInstance);
    this.waitstate = waitstate;
  }

  public static ProcessInstanceDelegate newInstance(WaitstateExecutable waitstate, ProcessInstance processInstance) {
    return processInstance != null ? new ProcessInstanceDelegateImpl(waitstate, processInstance) : null;
  }

  @Override
  public void defer(String period, Deferred action) {
    waitstate.defer(period, action);
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
