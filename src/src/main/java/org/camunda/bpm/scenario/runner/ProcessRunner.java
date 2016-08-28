package org.camunda.bpm.scenario.runner;

import org.camunda.bpm.engine.ProcessEngine;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ProcessRunner {

  interface ExecutableProcessRunner {

    ExecutableProcessRunner engine(ProcessEngine processEngine);

    ScenarioRun execute();

    interface StartingByKey extends ExecutableProcessRunner {

      StartingByKey fromBefore(String activityId);

      StartingByKey fromAfter(String activityId);

      ScenarioRun execute();

    }

    interface StartingByStarter extends ExecutableProcessRunner {

      ScenarioRun execute();

    }

    interface StartBy {

      StartingByKey startByKey(String processDefinitionKey);

      StartingByKey startByKey(String processDefinitionKey, Map<String, Object> variables);

      StartingByStarter startBy(ProcessStarter starter);

    }

  }
}
