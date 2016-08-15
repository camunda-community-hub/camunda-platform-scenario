package org.camunda.bpm.scenarios;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.task.Task;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class TaskWaitstate extends Waitstate<Task> {

  public TaskWaitstate(ProcessEngine processEngine, String executionId, String activityId) {
    super(processEngine, executionId, activityId);
  }

  @Override
  protected Task get() {
    return getTaskService().createTaskQuery().executionId(executionId).singleResult();
  }

  protected void leave() {
    getTaskService().complete(get().getId());
  }

  protected void leave(Map<String, Object> variables) {
    getTaskService().complete(get().getId(), variables);
  }

  public void completeTask() {
    leave();
  }

  public void completeTask(Map<String, Object> variables) {
    leave(variables);
  }

}
