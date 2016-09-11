package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.scenario.defer.Deferrable;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface TaskDelegate extends Task, ProcessInstanceAwareDelegate, Deferrable {

  /**
   * Complete this user task.
   */
  void complete();

  /**
   * Complete this user task and deliver a map of additional information
   * created by the human user (e.g. by filling out a form) and to be
   * stored as process instance variables.
   */
  void complete(Map<String, Object> variables);

}
