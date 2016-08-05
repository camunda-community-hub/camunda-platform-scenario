package org.camunda.bpm.specs;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;

public interface TaskAction {

  void execute(Task task, ProcessInstance processInstance);

}
