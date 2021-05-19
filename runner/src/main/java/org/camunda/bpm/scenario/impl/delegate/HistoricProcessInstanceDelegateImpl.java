package org.camunda.bpm.scenario.impl.delegate;

import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.scenario.defer.Deferred;
import org.camunda.bpm.scenario.delegate.ProcessInstanceDelegate;

import java.util.Map;

/**
 * @author Martin Schimak
 */
public class HistoricProcessInstanceDelegateImpl implements ProcessInstanceDelegate {

  private HistoricProcessInstance delegate;

  public HistoricProcessInstanceDelegateImpl(HistoricProcessInstance delegate) {
    this.delegate = delegate;
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
    throw new UnsupportedOperationException();
  }

  @Override
  public String getId() {
    return delegate.getId();
  }

  @Override
  public boolean isEnded() {
    return delegate.getRemovalTime() != null;
  }

  @Override
  public String getProcessInstanceId() {
    return getId();
  }

  @Override
  public String getTenantId() {
    return delegate.getTenantId();
  }

  @Override
  public void defer(String period, Deferred action) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Map<String, Object> getVariables() {
    throw new UnsupportedOperationException();
  }

}
