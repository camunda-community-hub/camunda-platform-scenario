package org.camunda.bpm.scenario.impl.util;

import org.camunda.bpm.engine.impl.calendar.DurationHelper;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.scenario.impl.util.Log.Action;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class Time {

  public static Date dateAfter(String period) {
    try {
      return new DurationHelper(period).getDateAfter();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void set(Date time) {
    Date currentTime = ClockUtil.getCurrentTime();
    ClockUtil.setCurrentTime(time);
    if (!time.equals(currentTime))
      Action.FastForward.log(null, null, null, null, null, null, null);
  }

  public static Date get() {
    return ClockUtil.getCurrentTime();
  }

  public static void reset() {
    Action.FinishingAt.log(null, null, null, null, null, null, null);
    ClockUtil.reset();
  }

  // ***
  // Necessary due to two possible Camunda Bugs with repeatable timers:
  // 1) in case their last execution is finished in the millisecond of
  // the timers due date, another timer is created.
  //
  // 2) Repeating timers always repeat at 0 milliseconds, so the first
  // repeating timer actually triggers a few millisceonds too early.
  //
  // I circumvent both problems here for the scenarios - which simulate
  // time anyway, by starting a process always at the "half" second and
  // assuming that any timers and simulated time periods will always
  // just work with an accuracy of seconds.
  private static final int milliseconds = 500;

  public static Date correct(Date date) {
    Calendar cal = Calendar.getInstance();
    cal.setTime(date);
    if (cal.get(Calendar.MILLISECOND) != milliseconds) {
      return new Date(date.getTime() + milliseconds);
    } else {
      return date;
    }
  }

  public static void init() {
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.MILLISECOND, milliseconds);
    ClockUtil.setCurrentTime(cal.getTime());
    Log.Action.StartingAt.log(null, null, null, null, null, null, null);
  }
  // ***

}
