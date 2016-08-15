package org.camunda.bpm.scenarios;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.task.Task;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class TaskWaitstate extends Waitstate<Task> {

  public TaskWaitstate(ProcessEngine processEngine, String executionId) {
    super(processEngine, executionId);
  }

  @Override
  protected Task get() {
    return getTaskService().createTaskQuery().executionId(executionId).singleResult();
  }

  protected void leave() {
    throw new NotImplementedException();
  };

  protected void leave(Map<String, Object> variables) {
    throw new NotImplementedException();
  };

  public void completeTask() {
    leave();
  }

  public void completeTask(Map<String, Object> variables) {
    leave(variables);
  }

}
