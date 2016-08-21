package org.camunda.bpm.scenario.runner;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.ScenarioAction;
import org.camunda.bpm.scenario.delegate.TaskDelegate;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class UserTaskWaitstate extends TaskDelegate {

  public UserTaskWaitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine, instance);
  }

  @Override
  protected Task getRuntimeDelegate() {
    return getTaskService().createTaskQuery().activityInstanceIdIn(historicDelegate.getId()).singleResult();
  }

  @Override
  protected ScenarioAction action(Scenario scenario) {
    return scenario.atUserTask(getActivityId());
  }

  protected void leave() {
    getTaskService().complete(getRuntimeDelegate().getId());
  }

  protected void leave(Map<String, Object> variables) {
    getTaskService().complete(getRuntimeDelegate().getId(), variables);
  }

  public void complete() {
    leave();
  }

  public void complete(Map<String, Object> variables) {
    leave(variables);
  }

}
