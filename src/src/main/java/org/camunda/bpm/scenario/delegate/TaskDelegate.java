package org.camunda.bpm.scenario.delegate;

import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.scenario.defer.Deferrable;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface TaskDelegate extends Task, ProcessInstanceAwareDelegate, Deferrable {

  void complete();

  void complete(Map<String, Object> variables);

}
