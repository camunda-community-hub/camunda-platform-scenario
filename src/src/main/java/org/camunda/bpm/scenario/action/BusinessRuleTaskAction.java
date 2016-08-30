package org.camunda.bpm.scenario.action;

import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface BusinessRuleTaskAction extends ServiceTaskAction {

  @Override
  void execute(final ExternalTaskDelegate externalTask) throws Exception;

}
