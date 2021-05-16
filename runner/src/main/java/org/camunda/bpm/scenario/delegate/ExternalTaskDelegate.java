package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.scenario.defer.Deferrable;

import java.util.Map;

/**
 * @author Martin Schimak
 */
public interface ExternalTaskDelegate extends ExternalTask, ProcessInstanceAwareDelegate, Deferrable {

  /**
   * Complete this external task.
   *
   * @since Camunda BPM 7.4.0
   */
  void complete();

  /**
   * Complete this external task and deliver a map of additional
   * information created by the external worker and to be stored
   * as process instance variables.
   *
   * @since Camunda BPM 7.4.0
   */
  void complete(Map<String, Object> variables);

  /**
   * Handle a BPMN Error raised by the external worker.
   *
   * @param errorCode of the BPMN error raised.
   * @since Camunda BPM 7.5.0
   */
  void handleBpmnError(String errorCode);

  /**
   * Handle a BPMN Error occurring with or raised by the
   * external worker and deliver a map of additional
   * information to be stored as process instance variables.
   *
   * @param errorCode of the BPMN error raised.
   * @param variables the process variables to be stored
   * @since Camunda BPM 7.10.0
   */
  void handleBpmnError(String errorCode, Map<String, Object> variables);

}
