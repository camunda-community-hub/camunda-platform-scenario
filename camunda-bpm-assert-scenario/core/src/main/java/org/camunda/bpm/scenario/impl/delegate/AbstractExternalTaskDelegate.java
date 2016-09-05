package org.camunda.bpm.scenario.impl.delegate;

import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;
import org.camunda.bpm.scenario.impl.WaitstateExecutable;

import java.util.Date;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class AbstractExternalTaskDelegate extends WaitstateExecutable<ExternalTask> implements ExternalTaskDelegate {

  public AbstractExternalTaskDelegate(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner, instance);
  }

  public String getId() {
    return delegate.getId();
  }

  public String getTopicName() {
    return delegate.getTopicName();
  }

  public String getWorkerId() {
    return delegate.getWorkerId();
  }

  public Date getLockExpirationTime() {
    return delegate.getLockExpirationTime();
  }

  public String getProcessInstanceId() {
    return delegate.getProcessInstanceId();
  }

  public String getActivityInstanceId() {
    return delegate.getActivityInstanceId();
  }

  public String getProcessDefinitionId() {
    return delegate.getProcessDefinitionId();
  }

  public String getProcessDefinitionKey() {
    return delegate.getProcessDefinitionKey();
  }

  public Integer getRetries() {
    return delegate.getRetries();
  }

  public String getErrorMessage() {
    return delegate.getErrorMessage();
  }

  public boolean isSuspended() {
    return delegate.isSuspended();
  }

  public String getTenantId() {
    return delegate.getTenantId();
  }

  public long getPriority() {
    return delegate.getPriority();
  }

}
