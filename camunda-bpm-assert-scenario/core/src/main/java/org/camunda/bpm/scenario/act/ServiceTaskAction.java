package org.camunda.bpm.scenario.act;

import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ServiceTaskAction extends Action<ExternalTaskDelegate> {

  @Override
  void execute(final ExternalTaskDelegate externalTask) throws Exception;

}
