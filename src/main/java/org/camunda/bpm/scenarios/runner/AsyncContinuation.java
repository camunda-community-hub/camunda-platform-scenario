package org.camunda.bpm.scenarios.runner;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.Job;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class AsyncContinuation extends Savepoint<Job> {

  protected String executionId;

  protected AsyncContinuation(ProcessEngine processEngine, String executionId) {
    super(processEngine);
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
