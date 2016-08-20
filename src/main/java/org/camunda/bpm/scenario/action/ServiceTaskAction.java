package org.camunda.bpm.scenario.action;

import org.camunda.bpm.scenario.runner.ServiceTaskWaitstate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ServiceTaskAction extends ScenarioAction<ServiceTaskWaitstate> {

  @Override
  void execute(ServiceTaskWaitstate externalTask);

}
