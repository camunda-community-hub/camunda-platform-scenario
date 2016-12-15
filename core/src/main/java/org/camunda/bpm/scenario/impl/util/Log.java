package org.camunda.bpm.scenario.impl.util;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.impl.persistence.entity.JobEntity;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.scenario.defer.Deferred;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Log {

  private static String space;
  private static String lastTime;
  private static String prefix;

  public enum Action {

    StartingAt {
      @Override
      public String toString() {
        return "Starting scenario at";
      }
    },
    FastForward {
      @Override
      public String toString() {
        return "Fast-forwarding time to";
      }
    },
    Deferring,
    Started,
    Completed,
    Finished,
    Canceled,
    ActingOn {
      @Override
      public String toString() {
        return "Acting on";
      }
    },
    Triggered

  }

  private static String LOGGER = "org.camunda.bpm.scenario";
  private static SimpleLogger logger =
      Api.feature("org.slf4j.Logger").isSupported() ? new Slf4jLog() : new JavaLog();
  private static DateFormat format = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");

  public static  void log(Action action, HistoricActivityInstance instance) {
    String message = String.format("%s %s '%s' (%s@%s)", Strings.rightpad(action.toString(), 9), instance.getActivityType(), Strings.trimAll(instance.getActivityName()), instance.getActivityId(), instance.getProcessInstanceId());
    if (action.equals(Action.Started)) {
      if (logger.isDebugEnabled())
        logger.debug(message(action, message));
    } else {
      if (logger.isInfoEnabled())
        logger.info(message(action, message));
    }
  }

  public static  void log(Action action, HistoricActivityInstance instance, Deferred deferred, Date executableAt) {
    String message;
    if (action.equals(Action.Deferring)) {
      message = String.format("%s action on %s '%s' until %s (%s@%s): %s", Strings.rightpad(action.toString(), 9), instance.getActivityType(), Strings.trimAll(instance.getActivityName()), format.format(executableAt), instance.getActivityId(), instance.getProcessInstanceId(), deferred);
    } else {
      message = String.format("%s deferred action on %s '%s' (%s@%s): %s", Strings.rightpad(action.toString(), 9), instance.getActivityType(), Strings.trimAll(instance.getActivityName()), instance.getActivityId(), instance.getProcessInstanceId(), deferred);
    }
    logger.info(message(action, message));
  }

  public static void log(Action action, Job instance) {
    JobEntity entity = (JobEntity) instance;
    String type = entity.getJobHandlerType();
    String config;
    if (Api.feature(JobEntity.class.getName(), "getJobHandlerConfigurationRaw").isSupported()) {
      config = entity.getJobHandlerConfigurationRaw();
    } else {
      try {
        config = (String) JobEntity.class.getMethod("getJobHandlerConfiguration").invoke(entity);
      } catch (Exception e) {
        config = "";
      }
    }
    String message = String.format("%s %s (%s@%s)", Strings.rightpad(action.toString(), 9), type, config, entity.getProcessInstanceId());
    logger.debug(message(action, message));
  }

  private static String message(Action action, String message) {
    String time = format.format(Time.get());
    if (lastTime == null) {
      logger.info(String.format("%s %s %s", space + "*", Action.StartingAt, time));
      prefix = space + "|";
    } else if (!time.equals(lastTime)) {
      logger.info(String.format("%s %s %s", space + "*", Action.FastForward, time));
      prefix = space + "|--"; space = space + "  ";
    } else  {
      prefix = action.equals(Action.ActingOn) ? space + "*" : space + "|";
    }
    lastTime = time;
    return String.format("%s %s", prefix, message);
  }

  private interface SimpleLogger {

    boolean isDebugEnabled();
    boolean isInfoEnabled();
    void debug(String message);
    void info(String message);

  }

  private static class Slf4jLog implements SimpleLogger {

    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LOGGER);

    @Override
    public boolean isDebugEnabled() {
      return log.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
      return log.isInfoEnabled();
    }

    @Override
    public void debug(String message) {
      log.debug(message);
    }

    @Override
    public void info(String message) {
      log.info(message);
    }

  }

  private static class JavaLog implements SimpleLogger {

    Logger log = Logger.getLogger(LOGGER);

    @Override
    public boolean isDebugEnabled() {
      return log.isLoggable(Level.FINE);
    }

    @Override
    public boolean isInfoEnabled() {
      return log.isLoggable(Level.INFO);
    }

    @Override
    public void debug(String message) {
      log.log(Level.FINE, message);
    }

    @Override
    public void info(String message) {
      log.log(Level.INFO, message);
    }

  }

  protected static void init() {
    space = "";
    lastTime = null;
  }

}
