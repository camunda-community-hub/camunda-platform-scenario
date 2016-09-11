package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.scenario.defer.Deferrable;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
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

}
