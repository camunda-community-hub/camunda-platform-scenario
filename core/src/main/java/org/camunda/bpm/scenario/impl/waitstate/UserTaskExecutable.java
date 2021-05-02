package org.camunda.bpm.scenario.impl.waitstate;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.act.Action;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;
import org.camunda.bpm.scenario.impl.delegate.AbstractTaskDelegate;

import java.util.Map;

/**
 * @author <a href="martin.schimak@plexiti.com">Martin Schimak</a>
 */
public class UserTaskExecutable extends AbstractTaskDelegate {

  public UserTaskExecutable(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
    super(runner, instance);
  }

  @Override
  protected Task getDelegate() {
    return getTaskService().createTaskQuery().activityInstanceIdIn(historicDelegate.getId()).singleResult();
  }

  @Override
  protected Action action(ProcessScenario scenario) {
    return scenario.waitsAtUserTask(getActivityId());
  }

  @Override
  public void complete() {
    getTaskService().complete(getDelegate().getId());
  }

  @Override
  public void complete(Map<String, Object> variables) {
    getTaskService().complete(getDelegate().getId(), variables);
  }

  @Override
  public void handleBpmnError(String errorCode) {
    getTaskService().handleBpmnError(getDelegate().getId(), errorCode);
  }

  @Override
  public void handleBpmnError(String errorCode, Map<String, Object> variables) {
    getTaskService().handleBpmnError(getDelegate().getId(), errorCode, null, variables);
  }

}
