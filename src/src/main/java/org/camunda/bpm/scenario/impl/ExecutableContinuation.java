package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.runtime.Job;

import java.util.Date;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ExecutableContinuation extends ExecutableJob {

  public ExecutableContinuation(ProcessRunnerImpl runner, Job job) {
    super(runner, job);
  }

  @Override
  public Date isExecutableAt() {
    return new Date(0);
  }

  @Override
  public void execute() {
    leave();
  }

}
