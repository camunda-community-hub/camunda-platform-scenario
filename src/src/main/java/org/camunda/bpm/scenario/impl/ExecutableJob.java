package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.runtime.Job;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class ExecutableJob extends AbstractExecutable<Job> {

  protected ExecutableJob(ProcessRunnerImpl runner, Job job) {
    super(runner);
    this.runtimeDelegate = job;
  }

  @Override
  public String getExecutionId() {
    return runtimeDelegate.getExecutionId();
  }

  @Override
  protected Job getRuntimeDelegate() {
    return getManagementService().createJobQuery().executionId(getExecutionId()).singleResult();
  }

  @Override
  protected void leave() {
    getManagementService().executeJob(runtimeDelegate.getId());
  }

}
