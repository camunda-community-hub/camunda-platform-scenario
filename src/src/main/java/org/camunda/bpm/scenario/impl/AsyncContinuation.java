package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.runtime.Job;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class AsyncContinuation extends AbstractSavepoint<Job> {

  protected String executionId;

  protected AsyncContinuation(ProcessRunnerImpl runner, String executionId) {
    super(runner);
    this.executionId = executionId;
    this.runtimeDelegate = getRuntimeDelegate();
  }

  @Override
  public String getExecutionId() {
    return executionId;
  }

  @Override
  protected Job getRuntimeDelegate() {
    return getManagementService().createJobQuery().executionId(executionId).singleResult();
  }

  @Override
  protected void leave() {
    getManagementService().executeJob(runtimeDelegate.getId());
  }

}
