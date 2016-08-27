package org.camunda.bpm.scenario.runner;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ProcessRunner {

  ProcessRunner engine(ProcessEngine processEngine);

  ProcessInstance execute();

  interface ProcessRunnerStartingByKey extends ProcessRunner {

    ProcessRunnerStartingByKey fromBefore(String activityId);

    ProcessRunnerStartingByKey fromAfter(String activityId);

    ProcessInstance execute();

  }

  interface ProcessRunnerStartingByStarter extends ProcessRunner {

    ProcessInstance execute();

  }

  interface ProcessRunnerStartBy {

    ProcessRunnerStartingByKey startBy(String processDefinitionKey);

    ProcessRunnerStartingByKey startBy(String processDefinitionKey, Map<String, Object> variables);

    ProcessRunnerStartingByStarter startBy(ProcessStarter starter);

  }

  interface CallActivityRunner {

  }

}
