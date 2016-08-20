package org.camunda.bpm.scenarios.waitstate;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.scenarios.Scenario;
import org.camunda.bpm.scenarios.WaitstateAction;
import org.camunda.bpm.scenarios.runner.Waitstate;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class IgnoredWaitstate extends Waitstate<Object> {

  public IgnoredWaitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine, instance);
  }

  @Override
  protected void execute(Scenario scenario) {
    // do nothing
  }

  @Override
  protected WaitstateAction<IgnoredWaitstate> action(Scenario scenario) {
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
  protected Object get() {
    return null;
  }

}
