package org.camunda.bpm.scenarios.waitstate;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.scenarios.Scenario;
import org.camunda.bpm.scenarios.delegate.TaskDelegate;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class UserTaskWaitstate extends TaskDelegate {

  protected UserTaskWaitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine, instance);
  }

  @Override
  protected Task get() {
    return getTaskService().createTaskQuery().activityInstanceIdIn(historicActivityInstance.getId()).singleResult();
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

  public void complete() {
    leave();
  }

  public void complete(Map<String, Object> variables) {
    leave(variables);
  }

}
