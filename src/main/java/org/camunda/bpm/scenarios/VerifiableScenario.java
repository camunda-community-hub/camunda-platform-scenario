package org.camunda.bpm.scenarios;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface VerifiableScenario {

  void hasStarted(String activityId);

  void hasPassed(String activityId);

  void hasCompleted(String activityId);

  void hasCanceled(String activityId);

}
