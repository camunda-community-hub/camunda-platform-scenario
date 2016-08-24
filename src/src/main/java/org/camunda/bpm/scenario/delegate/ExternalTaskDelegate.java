package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.scenario.runner.ScenarioRunnerImpl;
import org.camunda.bpm.scenario.runner.Waitstate;

import java.util.Date;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class ExternalTaskDelegate extends Waitstate<ExternalTask> implements ExternalTask {

  public ExternalTaskDelegate(ScenarioRunnerImpl runner, HistoricActivityInstance instance, String duration) {
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
