package org.camunda.bpm.scenarios;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class IgnoredWaitstate extends Waitstate{

  public IgnoredWaitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine, instance);
  }

  @Override
  protected void execute(Scenario scenario) {
    // do nothing
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
    throw new UnsupportedOperationException();
  }

}
