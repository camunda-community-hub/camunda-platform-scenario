package org.camunda.bpm.scenarios;

import org.camunda.bpm.scenarios.delegate.ProcessInstanceDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ScenarioHistory {

  void hasStarted(String activityId);

  void hasFinished(String activityId);

  void hasCompleted(String activityId);

  void hasCanceled(String activityId);

}
