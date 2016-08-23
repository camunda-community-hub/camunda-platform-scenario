package org.camunda.bpm.scenario.runner;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface CallActivityRunner {

  CallActivityRunner toBefore(String activityId, String... activityIds);

  CallActivityRunner toAfter(String activityId, String... activityIds);

}
