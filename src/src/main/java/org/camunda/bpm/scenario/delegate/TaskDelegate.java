package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.task.Task;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface TaskDelegate extends Task, ProcessInstanceAwareDelegate {

  void complete();

  void complete(Map<String, Object> variables);

}
