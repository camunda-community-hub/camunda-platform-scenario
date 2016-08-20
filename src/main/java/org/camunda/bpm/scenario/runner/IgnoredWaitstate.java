package org.camunda.bpm.scenario.runner;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.ScenarioAction;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class IgnoredWaitstate extends Waitstate<Object> {

  protected IgnoredWaitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine, instance);
  }

  @Override
  protected void execute(Scenario scenario) {
    // do nothing
  }

  @Override
  protected ScenarioAction<IgnoredWaitstate> action(Scenario scenario) {
    return null;
  }

  @Override
  protected void leave() {
    throw new UnsupportedOperationException();
  }

  @Override
  protected void leave(Map variables) {
    throw new UnsupportedOperationException();
  }

  @Override
  protected Object getRuntimeDelegate() {
    return null;
  }

}
