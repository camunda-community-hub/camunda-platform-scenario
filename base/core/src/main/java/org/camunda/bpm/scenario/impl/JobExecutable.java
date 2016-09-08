package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.runtime.Job;

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

}
