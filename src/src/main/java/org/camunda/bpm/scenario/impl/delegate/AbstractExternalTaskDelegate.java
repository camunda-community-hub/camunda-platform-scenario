package org.camunda.bpm.scenario.impl.delegate;

import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;
import org.camunda.bpm.scenario.impl.AbstractWaitstate;

import java.util.Date;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class AbstractExternalTaskDelegate extends AbstractWaitstate<ExternalTask> implements ExternalTaskDelegate {

  public AbstractExternalTaskDelegate(ProcessRunnerImpl runner, HistoricActivityInstance instance, String duration) {
    super(runner, instance, duration);
  }

  public String getId() {
    return runtimeDelegate.getId();
  }

  public String getTopicName() {
    return runtimeDelegate.getTopicName();
  }

  public String getWorkerId() {
    return runtimeDelegate.getWorkerId();
  }

  public Date getLockExpirationTime() {
    return runtimeDelegate.getLockExpirationTime();
  }

  public String getProcessInstanceId() {
    return runtimeDelegate.getProcessInstanceId();
  }

  public String getActivityInstanceId() {
    return runtimeDelegate.getActivityInstanceId();
  }

  public String getProcessDefinitionId() {
    return runtimeDelegate.getProcessDefinitionId();
  }

  public String getProcessDefinitionKey() {
    return runtimeDelegate.getProcessDefinitionKey();
  }

  public Integer getRetries() {
    return runtimeDelegate.getRetries();
  }

  public String getErrorMessage() {
    return runtimeDelegate.getErrorMessage();
  }

  public boolean isSuspended() {
    return runtimeDelegate.isSuspended();
  }

  public String getTenantId() {
    return runtimeDelegate.getTenantId();
  }

  public long getPriority() {
    return runtimeDelegate.getPriority();
  }

}
