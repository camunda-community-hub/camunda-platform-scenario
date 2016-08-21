package org.camunda.bpm.scenario;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ScenarioHistory {

  void hasStarted(String activityId);

  void hasFinished(String activityId);

  /**
   * @since Camunda BPM 7.1.0-Final
   */
  void hasCompleted(String activityId);

  /**
   * @since Camunda BPM 7.1.0-Final
   */
  void hasCanceled(String activityId);

}
