package org.camunda.bpm.scenario.run;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.Scenario;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ProcessRunner extends Runner {

  interface ExecutableRunner {

    /**
     * Provide a custom process engine to the scenario
     * run. You just need to call it in case you ramp
     * up more than one process engine during your tests.
     *
     * @param processEngine to be used by the scenario run
     */
    ExecutableRunner engine(ProcessEngine processEngine);

    /**
     * Execute the fully defined scenario run.
     *
     * @return an executed scenario
     */
    Scenario execute();

    /**
     * Run another new process instance by means of the
     * scenario interface provided as parameter.
     * .
     * @param scenario interface to be used for running the process
     * instance
     */
    StartableRunner run(ProcessScenario scenario);

    interface StartingByKey extends ExecutableRunner {

      /**
       * Run the new process instance not from its beginning, but
       * just from before the activity id provided as parameter.
       *
       * @param activityId from before which the new process instance
       * should be started
       */
      StartingByKey fromBefore(String activityId);

      /**
       * Run the new process instance not from its beginning, but
       * just from after the activity id provided as parameter.
       *
       * @param activityId from after which the new process instance
       * should be started
       */
      StartingByKey fromAfter(String activityId);

      /**
       * Execute the fully defined scenario run.
       *
       * @return an executed scenario
       */
      Scenario execute();

    }

    interface StartingByMessage extends ExecutableRunner {

      /**
       * Execute the fully defined scenario run.
       *
       * @return an executed scenario
       */
      Scenario execute();

    }

    interface StartingByStarter extends ExecutableRunner {

      /**
       * Execute the fully defined scenario run.
       *
       * @return an executed scenario
       */
      Scenario execute();

    }

  }

  interface StartableRunner {

    /**
     * Start the new process instance by providing a process definition key.
     *
     * @param processDefinitionKey to be used to start a process instance
     */
    ExecutableRunner.StartingByKey startByKey(String processDefinitionKey);

    /**
     * Start the new process instance by providing a process definition key
     * and provide a few process instance variables.
     *
     * @param processDefinitionKey to be used to start a process instance
     * @param variables to be used as process instance variables from the start on.
     */
    ExecutableRunner.StartingByKey startByKey(String processDefinitionKey, Map<String, Object> variables);

    /**
     * Start the new process instance by providing a message name.
     *
     * @param messageName to be used to start a process instance
     */
    ExecutableRunner.StartingByMessage startByMessage(String messageName);

    /**
     * Start the new process instance by providing a message name
     * and provide a few process instance variables.
     *
     * @param messageName to be used to start a process instance
     * @param variables to be used as process instance variables from the start on.
     */
    ExecutableRunner.StartingByMessage startByMessage(String messageName, Map<String, Object> variables);

    /**
     * Start the new process instance by means of custom code. Implement
     * the ProcessStarter.start() method to create a process instance.
     * @param starter interface which delivers a new process instance
     */
    ExecutableRunner.StartingByStarter startBy(ProcessStarter starter);

  }

}
