package org.camunda.bpm.scenario.delegate;

import java.util.Map;

/**
 * @author Martin Schimak
 */
public interface MockedCallActivityDelegate extends TaskDelegate {

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
   * Handle a BPMN error raised within the call activity.
   *
   * @param errorCode of the BPMN error raised
   * @since Camunda BPM 7.5.0
   */
  void handleBpmnError(String errorCode);

  /**
   * Handle a BPMN error raised within the call activity and
   * deliver a map of additional information to be stored as
   * process instance variables.
   *
   * @param errorCode of the BPMN error raised
   * @param variables the process variables to be stored
   * @since Camunda BPM 7.10.0
   */
  void handleBpmnError(String errorCode, Map<String, Object> variables);

  /**
   * Handle a BPMN escalation raised within the call activity.
   *
   * @param escalationCode of the BPMN escalation raised
   * @since Camunda BPM 7.12.0
   */
  void handleEscalation(String escalationCode);

  /**
   * Handle a BPMN escalation raised within the call activity
   * and deliver a map of additional information to be stored as
   * process instance variables.
   *
   * @param escalationCode of the BPMN escalation raised
   * @param variables      the process variables to be stored
   * @since Camunda BPM 7.12.0
   */
  void handleEscalation(String escalationCode, Map<String, Object> variables);

}
