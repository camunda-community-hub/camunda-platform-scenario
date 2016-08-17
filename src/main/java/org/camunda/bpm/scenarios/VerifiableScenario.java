package org.camunda.bpm.scenarios;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface VerifiableScenario {

  void hasCompleted(String activityId);

  void hasPassed(String activityId);

  void hasCanceled(String activityId);

  void isWaitingAt(String activityId);

}
