package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.externaltask.ExternalTask;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ExternalTaskDelegate extends ExternalTask {

  void complete();

  void complete(Map<String, Object> variables);

  void handleBpmnError(String errorCode);

}
