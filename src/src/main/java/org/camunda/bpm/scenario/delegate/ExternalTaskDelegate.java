package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.scenario.defer.Deferrable;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ExternalTaskDelegate extends ExternalTask, ProcessInstanceAwareDelegate, Deferrable {

  /**
   * @since Camunda BPM 7.4.0
   */
  void complete();

  /**
   * @since Camunda BPM 7.4.0
   */
  void complete(Map<String, Object> variables);

  /**
   * @since Camunda BPM 7.5.0
   */
  void handleBpmnError(String errorCode);

}
