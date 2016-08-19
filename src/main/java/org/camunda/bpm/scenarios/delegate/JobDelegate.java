package org.camunda.bpm.scenarios.delegate;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.scenarios.runner.Waitstate;

import java.util.Date;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class JobDelegate extends Waitstate<Job> implements Job {

  public JobDelegate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine, instance);
  }

  public String getId() {
    return delegate.getId();
  }

  public Date getDuedate() {
    return delegate.getDuedate();
  }

  public String getProcessInstanceId() {
    return delegate.getProcessInstanceId();
  }

  public String getProcessDefinitionId() {
    return delegate.getProcessDefinitionId();
  }

  public String getProcessDefinitionKey() {
    return delegate.getProcessDefinitionKey();
  }

  public int getRetries() {
    return delegate.getRetries();
  }

  public String getExceptionMessage() {
    return delegate.getExceptionMessage();
  }

  public String getDeploymentId() {
    return delegate.getDeploymentId();
  }

  public String getJobDefinitionId() {
    return delegate.getJobDefinitionId();
  }

  public boolean isSuspended() {
    return delegate.isSuspended();
  }

  public long getPriority() {
    return delegate.getPriority();
  }

  public String getTenantId() {
    return delegate.getTenantId();
  }

}
