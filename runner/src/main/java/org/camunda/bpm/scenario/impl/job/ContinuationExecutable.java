package org.camunda.bpm.scenario.impl.job;

import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.scenario.impl.JobExecutable;
import org.camunda.bpm.scenario.impl.ProcessInstanceRunner;

import java.util.Date;

/**
 * @author Martin Schimak
 */
public class ContinuationExecutable extends JobExecutable {

  public ContinuationExecutable(ProcessInstanceRunner runner, Job job) {
    super(runner, job);
  }

  @Override
  public Date isExecutableAt() {
    return new Date(0);
  }

}
