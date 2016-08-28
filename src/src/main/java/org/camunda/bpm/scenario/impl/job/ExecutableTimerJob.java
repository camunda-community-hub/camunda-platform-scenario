package org.camunda.bpm.scenario.impl.job;

import org.camunda.bpm.engine.impl.persistence.entity.TimerEntity;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.scenario.impl.ExecutableJob;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ExecutableTimerJob extends ExecutableJob {

  // for motivation see below init()
  private static final int milliseconds = 500;

  public ExecutableTimerJob(ProcessRunnerImpl runner, Job job) {
    super(runner, job);
  }

  @Override
  public Date isExecutableAt() {
    // for motivation see below init()
    Calendar cal = Calendar.getInstance();
    cal.setTime(delegate.getDuedate());
    if (cal.get(Calendar.MILLISECOND) != milliseconds) {
      return new Date(delegate.getDuedate().getTime() + milliseconds);
    } else {
      return delegate.getDuedate();
    }
  }

  @Override
  public void executeJob() {
    ClockUtil.setCurrentTime(isExecutableAt());
    super.executeJob();
  }

  public static void init() {
    // Necessary due to a possible Camunda Bug with repeatable timers
    // in case their last execution is finished in the millisecond of
    // the timers due date. Since repeating timers always repeat at 0
    // milliseconds, one can circumvent this here when simulating time.
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.MILLISECOND, milliseconds);
    ClockUtil.setCurrentTime(cal.getTime());
  }

}
