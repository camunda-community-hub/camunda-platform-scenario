package org.camunda.bpm.scenario.action;

import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ServiceTaskAction extends ScenarioAction<ExternalTaskDelegate> {

  @Override
  void execute(ExternalTaskDelegate externalTask);

}
