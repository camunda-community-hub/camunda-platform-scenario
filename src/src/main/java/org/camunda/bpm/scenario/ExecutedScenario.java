package org.camunda.bpm.scenario;

import org.camunda.bpm.engine.runtime.ProcessInstance;

import java.util.List;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ExecutedScenario {

  ProcessInstance getInstance(ProcessScenario scenario);

  List<ProcessInstance> getInstances(ProcessScenario scenario);

}
