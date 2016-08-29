package org.camunda.bpm.scenario.impl.job;

import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.scenario.impl.ExecutableJob;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;
import org.camunda.bpm.scenario.impl.util.Time;

import java.util.Date;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ExecutableTimerJob extends ExecutableJob {

  public ExecutableTimerJob(ProcessRunnerImpl runner, Job job) {
    super(runner, job);
  }

  @Override
  public Date isExecutableAt() {
    return Time.correct(delegate.getDuedate());
  }

  @Override
  public void executeJob() {
    Time.set(isExecutableAt());
    super.executeJob();
  }

}
