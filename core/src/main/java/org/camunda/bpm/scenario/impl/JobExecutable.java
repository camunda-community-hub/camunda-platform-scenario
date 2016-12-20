package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.impl.persistence.entity.JobEntity;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.scenario.impl.util.Api;
import org.camunda.bpm.scenario.impl.util.Log;
import org.camunda.bpm.scenario.impl.util.Log.Action;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class JobExecutable extends AbstractExecutable<Job> {

  protected JobExecutable(ProcessRunnerImpl runner, Job job) {
    super(runner);
    this.delegate = job;
  }

  @Override
  public String getExecutionId() {
    return delegate.getExecutionId();
  }

  @Override
  protected Job getDelegate() {
    return getManagementService().createJobQuery().jobId(delegate.getId()).singleResult();
  }

  protected void executeJob() {
    log();
    getManagementService().executeJob(delegate.getId());
  }

  @Override
  public void execute() {
    executeJob();
    runner.setExecuted();
  }

  @Override
  public int compareTo(AbstractExecutable other) {
    int compare = super.compareTo(other);
    return compare == 0 ? idComparator.compare(delegate.getId(), ((JobExecutable) other).delegate.getId()) : compare;
  }

  private void log() {
    JobEntity entity = (JobEntity) delegate;
    String type = entity.getJobHandlerType();
    String config;
    if (Api.feature(JobEntity.class.getName(), "getJobHandlerConfigurationRaw").isSupported()) {
      config = entity.getJobHandlerConfigurationRaw();
    } else {
      try {
        config = (String) JobEntity.class.getMethod("getJobHandlerConfiguration").invoke(entity);
      } catch (Exception e) {
        config = "";
      }
    }
    Action.Executing_Job.log(
        type,
        config,
        null,
        getRepositoryService().createProcessDefinitionQuery().processDefinitionId(runner.processInstance.getProcessDefinitionId()).singleResult().getKey(),
        runner.processInstance.getId(),
        null,
        null
    );
  }

}
