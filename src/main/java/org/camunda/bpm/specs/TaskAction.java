package org.camunda.bpm.specs;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface TaskAction {

  void execute(Task task, ProcessInstance processInstance);

}
