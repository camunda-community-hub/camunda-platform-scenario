package org.camunda.bpm.scenario.impl.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Log {

  private static String LOGGER = "org.camunda.bpm.scenario";
  private static ScenarioLog scenarioLog = Api.feature("org.slf4j.Logger").isSupported() ? new Slf4jLog() : new JavaLog();

  private static String space;
  private static String prefix;

  private interface ScenarioLog {

    boolean isDebugEnabled();
    void debug(String message);
    void info(String message);

  }

  private static class Slf4jLog implements ScenarioLog {

    org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LOGGER);

    @Override
    public boolean isDebugEnabled() {
      return log.isDebugEnabled();
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

  private static class JavaLog implements ScenarioLog {

    Logger log = Logger.getLogger(LOGGER);

    @Override
    public boolean isDebugEnabled() {
      return log.isLoggable(Level.FINE);
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

  private interface ScenarioLoggable {

    String instanceFormat = "%s %s %s LABEL(%s @ %s # %s)";
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd' 'HH:mm:ss");

    void log(String activityType, String activityName, String activityId, String processDefinitionKey, String processInstanceId, String actionId, Date time);

  }

  public enum Action implements ScenarioLoggable {

    StartingAt {

      private void init() {
        space = "";
        prefix = "|";
      }

      public void log(String activityType, String activityName, String activityId, String processDefinitionKey, String processInstanceId, String actionId, Date time) {
        init();
        scenarioLog.info(String.format("%s Starting scenario at %s", "*", dateFormat.format(Time.get())));
      }

    },

    FastForward {

      public void log(String activityType, String activityName, String activityId, String processDefinitionKey, String processInstanceId, String actionId, Date time) {
        scenarioLog.info(String.format("%s Fast-forwarding scenario to %s", prefix, dateFormat.format(Time.get())));
        prefix = space + "|--";
        space = space + "  ";
      }

    },

    FinishingAt {

      private void init() {
        space = "";
        prefix = "|";
      }

      public void log(String activityType, String activityName, String activityId, String processDefinitionKey, String processInstanceId, String actionId, Date time) {
        scenarioLog.info(String.format("%s Finishing scenario at %s", space + "*", dateFormat.format(Time.get())));
        init();
      }

    },

    ActingOn {

      public void log(String activityType, String activityName, String activityId, String processDefinitionKey, String processInstanceId, String actionId, Date time) {
        String message;
        if (activityName != null) {
          message = String.format(ScenarioLoggable.instanceFormat.replace("LABEL", "'%s' "),
              space + "*",
              Strings.rightpad(toString(), 9),
              Strings.rightpad(activityType, 18),
              Strings.trimAll(activityName),
              activityId,
              processDefinitionKey,
              processInstanceId);
        } else {
          message = String.format(ScenarioLoggable.instanceFormat.replace("LABEL", ""),
              space + "*",
              Strings.rightpad(toString(), 9),
              Strings.rightpad(activityType, 18),
              activityId,
              processDefinitionKey,
              processInstanceId);
        }
        scenarioLog.info(message);
        prefix = space + "|";
      }

      @Override
      public String toString() {
        return "Acting on";
      }

    },

    Deferring_Action {

      public void log(String activityType, String activityName, String activityId, String processDefinitionKey, String processInstanceId, String actionId, Date time) {
        if (scenarioLog.isDebugEnabled()) {
          String message = String.format("%s %s %s '%s' until %s (%s @ %s # %s : %s)",
              prefix,
              Strings.rightpad("Deferring", 9),
              Strings.rightpad("action on", 18),
              Strings.trimAll(activityName),
              dateFormat.format(time),
              activityId,
              processDefinitionKey,
              processInstanceId,
              actionId);
          scenarioLog.debug(message);
          prefix = space + "|";
        }
      }

    },

    Executing_Action {

      public void log(String activityType, String activityName, String activityId, String processDefinitionKey, String processInstanceId, String actionId, Date time) {
        String message = String.format("%s %s %s '%s' (%s @ %s # %s : %s)",
            prefix,
            Strings.rightpad("Executing", 9),
            Strings.rightpad("deferred action on", 18),
            Strings.trimAll(activityName),
            activityId,
            processDefinitionKey,
            processInstanceId,
            actionId);
        scenarioLog.info(message);
        prefix = space + "|";
      }

    },

    Executing_Job {

      public void log(String activityType, String activityName, String activityId, String processDefinitionKey, String processInstanceId, String actionId, Date time) {
        if (scenarioLog.isDebugEnabled()) {
          String message = String.format("%s %s %s (%s @ %s # %s)",
              prefix,
              Strings.rightpad(toString(), 9),
              Strings.rightpad(activityType, 18),
              Strings.trimAll(activityName),
              processDefinitionKey,
              processInstanceId
          );
          scenarioLog.debug(message);
          prefix = space + "|";
        }
      }

    },

    Started {

      public void log(String activityType, String activityName, String activityId, String processDefinitionKey, String processInstanceId, String actionId, Date time) {
        if (scenarioLog.isDebugEnabled()) {
          scenarioLog.debug(message(this, activityType, activityName, activityId, processDefinitionKey, processInstanceId));
          prefix = space + "|";
        }
      }

    },

    Finished {

      public void log(String activityType, String activityName, String activityId, String processDefinitionKey, String processInstanceId, String actionId, Date time) {
        scenarioLog.info(message(this, activityType, activityName, activityId, processDefinitionKey, processInstanceId));
        prefix = space + "|";
      }

    },

    Canceled {

      public void log(String activityType, String activityName, String activityId, String processDefinitionKey, String processInstanceId, String actionId, Date time) {
        scenarioLog.info(message(this, activityType, activityName, activityId, processDefinitionKey, processInstanceId));
        prefix = space + "|";
      }

    },

    Completed {

      public void log(String activityType, String activityName, String activityId, String processDefinitionKey, String processInstanceId, String actionId, Date time) {
        scenarioLog.info(message(this, activityType, activityName, activityId, processDefinitionKey, processInstanceId));
        prefix = space + "|";
      }

    },

  }

  private static String message(Action action, String activityType, String activityName, String activityId, String processDefinitionKey, String processInstanceId) {
    String message;
    if (activityName != null && !activityName.equals("")) {
      message = String.format(ScenarioLoggable.instanceFormat.replace("LABEL", "'%s' "),
          prefix,
          Strings.rightpad(action.toString(), 9),
          Strings.rightpad(activityType, 18),
          Strings.trimAll(activityName),
          activityId,
          processDefinitionKey,
          processInstanceId);
    } else {
      message = String.format(ScenarioLoggable.instanceFormat.replace("LABEL", ""),
          prefix,
          Strings.rightpad(action.toString(), 9),
          Strings.rightpad(activityType, 18),
          activityId,
          processDefinitionKey,
          processInstanceId);
    }
    return message;
  }

}
