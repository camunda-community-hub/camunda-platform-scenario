package org.camunda.bpm.scenario.impl.delegate;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.scenario.delegate.TimerJobDelegate;
import org.camunda.bpm.scenario.impl.ExecutableWaitstate;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;

import java.util.Date;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class AbstractTimerJobDelegate extends ExecutableWaitstate<Job> implements TimerJobDelegate {

  public AbstractTimerJobDelegate(ProcessRunnerImpl runner, HistoricActivityInstance instance, String duration) {
    super(runner, instance, duration);
  }

  public String getId() {
    return runtimeDelegate.getId();
  }

  public Date getDuedate() {
    return runtimeDelegate.getDuedate();
  }

  public String getProcessInstanceId() {
    return runtimeDelegate.getProcessInstanceId();
  }

  public String getProcessDefinitionId() {
    return runtimeDelegate.getProcessDefinitionId();
  }

  public String getProcessDefinitionKey() {
    return runtimeDelegate.getProcessDefinitionKey();
  }

  public int getRetries() {
    return runtimeDelegate.getRetries();
  }

  public String getExceptionMessage() {
    return runtimeDelegate.getExceptionMessage();
  }

  public String getDeploymentId() {
    return runtimeDelegate.getDeploymentId();
  }

  public String getJobDefinitionId() {
    return runtimeDelegate.getJobDefinitionId();
  }

  public boolean isSuspended() {
    return runtimeDelegate.isSuspended();
  }

  public long getPriority() {
    return runtimeDelegate.getPriority();
  }

  public String getTenantId() {
    return runtimeDelegate.getTenantId();
  }

}
