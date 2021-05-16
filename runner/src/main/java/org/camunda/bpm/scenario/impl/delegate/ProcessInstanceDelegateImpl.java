package org.camunda.bpm.scenario.impl.delegate;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.defer.Deferred;
import org.camunda.bpm.scenario.delegate.ProcessInstanceDelegate;
import org.camunda.bpm.scenario.impl.WaitstateExecutable;

import java.util.Map;

/**
 * @author Martin Schimak
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

  @Override
  public Map<String, Object> getVariables() {
    return waitstate.getVariables();
  }

  @Override
  public String getProcessDefinitionId() {
    return delegate.getProcessDefinitionId();
  }

  @Override
  public String getBusinessKey() {
    return delegate.getBusinessKey();
  }

  @Override
  public String getRootProcessInstanceId() {
    return delegate.getRootProcessInstanceId();
  }

  @Override
  public String getCaseInstanceId() {
    return delegate.getCaseInstanceId();
  }

  @Override
  public boolean isSuspended() {
    return delegate.isSuspended();
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public boolean isEnded() {
    return delegate.isEnded();
  }

  @Override
  public String getProcessInstanceId() {
    return delegate.getProcessInstanceId();
  }

  @Override
  public String getTenantId() {
    return delegate.getTenantId();
  }

}
