package org.camunda.bpm.scenario.runner;

import org.camunda.bpm.engine.ProcessEngine;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ProcessRunner {

  ProcessRunner engine(ProcessEngine processEngine);

  ScenarioRun execute();

  interface ProcessRunnerStartingByKey extends ProcessRunner {

    ProcessRunnerStartingByKey fromBefore(String activityId);

    ProcessRunnerStartingByKey fromAfter(String activityId);

    ScenarioRun execute();

  }

  interface ProcessRunnerStartingByStarter extends ProcessRunner {

    ScenarioRun execute();

  }

  interface ProcessRunnerStartBy {

    ProcessRunnerStartingByKey startByKey(String processDefinitionKey);

    ProcessRunnerStartingByKey startByKey(String processDefinitionKey, Map<String, Object> variables);

    ProcessRunnerStartingByStarter startBy(ProcessStarter starter);

  }

  interface CallActivityRunner {

  }

}
