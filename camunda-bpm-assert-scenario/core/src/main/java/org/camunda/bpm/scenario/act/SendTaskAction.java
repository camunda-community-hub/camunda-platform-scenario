package org.camunda.bpm.scenario.act;

import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface SendTaskAction extends ServiceTaskAction {

  @Override
  void execute(final ExternalTaskDelegate externalTask) throws Exception;

}
