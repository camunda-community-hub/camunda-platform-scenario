package org.camunda.bpm.scenario.run;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface Runnable {

  /**
   * Callback method which is called during the scenario
   * run when an activity instance of the given activity
   * id has been started.
   *
   * @since Camunda BPM 7.0.0-Final
   */
  void hasStarted(String activityId);

  /**
   * Callback method which is called during the scenario
   * run when an activity instance of the given activity
   * id has been finished (in other words has been either
   * completed or canceled).
   *
   * @since Camunda BPM 7.0.0-Final
   */
  void hasFinished(String activityId);

  /**
   * Callback method which is called during the scenario
   * run when an activity instance of the given activity
   * id has been (successfully) completed.
   *
   * @since Camunda BPM 7.1.0-Final
   */
  void hasCompleted(String activityId);

  /**
   * Callback method which is called during the scenario
   * run when an activity instance of the given activity
   * id has been (unsuccessfully) cancelled.
   *
   * @since Camunda BPM 7.1.0-Final
   */
  void hasCanceled(String activityId);

}
