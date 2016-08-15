package org.camunda.bpm.scenarios;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class CallActivityWaitstate extends Waitstate<ProcessInstance> {

  public CallActivityWaitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine, instance);
  }

  @Override
  protected ProcessInstance get() {
    return getRuntimeService().createProcessInstanceQuery().processInstanceId(instance.getCalledProcessInstanceId()).singleResult();
  }

  protected static String getActivityType() {
    return "callActivity";
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

  public ScenarioRunner scenarioRunner() {
    return new ScenarioRunner().running(get());
  }

  public ProcessInstance getCalledProcessInstance() {
    return get();
  }

}
