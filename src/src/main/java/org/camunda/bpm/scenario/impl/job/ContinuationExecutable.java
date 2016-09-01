package org.camunda.bpm.scenario.impl.job;

import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.scenario.impl.JobExecutable;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;

import java.util.Date;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ContinuationExecutable extends JobExecutable {

  public ContinuationExecutable(ProcessRunnerImpl runner, Job job) {
    super(runner, job);
  }

  @Override
  public Date isExecutableAt() {
    return new Date(0);
  }

}
