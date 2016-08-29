package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.scenario.action.DeferredAction;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface DeferrableDelegate {

  void defer(String period, DeferredAction action);

}
