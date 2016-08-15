package org.camunda.bpm.scenarios;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.Job;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class AsyncContinuation extends Savepoint<Job> {

  public AsyncContinuation(ProcessEngine processEngine, String executionId) {
    super(processEngine, executionId);
  }

  @Override
  protected Job get() {
    return getManagementService().createJobQuery().executionId(executionId).singleResult();
  }

  @Override
  protected void leave() {
    getManagementService().executeJob(get().getId());
  }

}
