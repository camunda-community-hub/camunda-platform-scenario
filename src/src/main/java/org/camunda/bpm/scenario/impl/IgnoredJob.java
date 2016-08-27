package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.runtime.Job;

import java.util.Date;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class IgnoredJob extends ExecutableJob {

  protected IgnoredJob(ProcessRunnerImpl runner, Job job) {
    super(runner, job);
  }

  @Override
  public void execute() {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getExecutionId() {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void leave() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Date isExecutableAt() {
    throw new UnsupportedOperationException();
  }

}
