package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.runtime.Job;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class ExecutableJob extends AbstractExecutable<Job> {

  protected ExecutableJob(ProcessRunnerImpl runner, Job job) {
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

  @Override
  protected void leave() {
    getManagementService().executeJob(delegate.getId());
  }

  @Override
  public void execute() {
    leave();
    runner.setExecuted(null);
  }

}
