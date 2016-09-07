package org.camunda.bpm.scenario.run;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface Runnable {

  /**
   * @since Camunda BPM 7.0.0-Final
   */
  void hasStarted(String activityId);

  /**
   * @since Camunda BPM 7.0.0-Final
   */
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
