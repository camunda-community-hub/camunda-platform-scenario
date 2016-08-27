package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.impl.persistence.entity.MessageEntity;
import org.camunda.bpm.engine.runtime.Job;

import java.util.Date;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ExecutableJob extends AbstractExecutable<Job> {

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

  @Override
  public Date isExecutableAt() {
    if (runtimeDelegate instanceof MessageEntity) {
      MessageEntity entity = (MessageEntity) runtimeDelegate;
      if ("async-continuation".equals(entity.getJobHandlerType()))
        return new Date(0);
    }
    return runtimeDelegate.getDuedate();
  }

  @Override
  public void execute() {
    leave();
  }

}
