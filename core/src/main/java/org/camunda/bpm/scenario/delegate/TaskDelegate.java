package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.scenario.defer.Deferrable;

import java.util.Map;

/**
 * @author <a href="martin.schimak@plexiti.com">Martin Schimak</a>
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

  /**
   * Handle a BPMN Error occurring with or raised by the human user.
   *
   * @param errorCode of the BPMN error raised.
   * @since Camunda BPM 7.12.0
   */
  void handleBpmnError(String errorCode);

  /**
   * Handle a BPMN Error occurring with or raised by the human user
   * and deliver a map of additional information to be stored as
   * process instance variables.
   *
   * @param errorCode of the BPMN error raised.
   * @since Camunda BPM 7.12.0
   */
  void handleBpmnError(String errorCode, Map<String, Object> variables);

}
