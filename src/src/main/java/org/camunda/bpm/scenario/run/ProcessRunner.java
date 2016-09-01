package org.camunda.bpm.scenario.run;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.scenario.ExecutedScenario;
import org.camunda.bpm.scenario.ProcessScenario;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ProcessRunner {

  interface ExecutableRunner {

    ExecutableRunner engine(ProcessEngine processEngine);

    ExecutedScenario execute();

    ToBeStartedBy run(ProcessScenario scenario);

    interface StartingByKey extends ExecutableRunner {

      StartingByKey fromBefore(String activityId);

      StartingByKey fromAfter(String activityId);

      ExecutedScenario execute();

    }

    interface StartingByMessage extends ExecutableRunner {

      ExecutedScenario execute();

    }

    interface StartingByStarter extends ExecutableRunner {

      ExecutedScenario execute();

    }

  }

  interface ToBeStartedBy {

    ExecutableRunner.StartingByKey startByKey(String processDefinitionKey);

    ExecutableRunner.StartingByKey startByKey(String processDefinitionKey, Map<String, Object> variables);

    ExecutableRunner.StartingByMessage startByMessage(String messageName);

    ExecutableRunner.StartingByMessage startByMessage(String messageName, Map<String, Object> variables);

    ExecutableRunner.StartingByStarter startBy(ProcessStarter starter);

  }

}
