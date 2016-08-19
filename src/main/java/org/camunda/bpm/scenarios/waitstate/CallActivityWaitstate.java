package org.camunda.bpm.scenarios.waitstate;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenarios.Scenario;
import org.camunda.bpm.scenarios.runner.ScenarioRunnerImpl;
import org.camunda.bpm.scenarios.delegate.ProcessInstanceDelegate;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class CallActivityWaitstate extends ProcessInstanceDelegate {

  protected CallActivityWaitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine, instance);
  }

  @Override
  protected ProcessInstance get() {
    return getRuntimeService().createProcessInstanceQuery().processInstanceId(historicActivityInstance.getCalledProcessInstanceId()).singleResult();
  }

  protected static String getActivityType() {
    return "";
  }

  @Override
  protected void execute(Scenario scenario) {
    scenario.atCallActivity(getActivityId()).execute(this);
  }

  protected void leave() {
    throw new UnsupportedOperationException();
  }

  protected void leave(Map<String, Object> variables) {
    throw new UnsupportedOperationException();
  }

  public ScenarioRunnerImpl runner() {
    return new ScenarioRunnerImpl().running(get());
  }

}
