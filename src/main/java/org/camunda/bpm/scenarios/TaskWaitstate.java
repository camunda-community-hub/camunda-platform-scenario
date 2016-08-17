package org.camunda.bpm.scenarios;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.task.Task;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class TaskWaitstate extends Waitstate<Task> {

  protected TaskWaitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine, instance);
  }

  @Override
  protected Task get() {
    return getTaskService().createTaskQuery().activityInstanceIdIn(instance.getId()).singleResult();
  }

  protected static String getActivityType() {
    return "userTask";
  }

  @Override
  protected void execute(Scenario scenario) {
    scenario.atTask(getActivityId()).execute(this);
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

  public Task getTask() {
    return get();
  }

}
