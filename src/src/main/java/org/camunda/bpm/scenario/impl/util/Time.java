package org.camunda.bpm.scenario.impl.util;

import org.camunda.bpm.engine.impl.calendar.DurationHelper;
import org.camunda.bpm.engine.impl.util.ClockUtil;

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
    ClockUtil.setCurrentTime(time);
  }

}
