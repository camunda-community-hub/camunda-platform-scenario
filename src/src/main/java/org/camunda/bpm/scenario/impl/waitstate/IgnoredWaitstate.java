package org.camunda.bpm.scenario.impl.waitstate;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.ScenarioAction;
import org.camunda.bpm.scenario.impl.ExecutableWaitstate;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class IgnoredWaitstate extends ExecutableWaitstate<Object> {

  public IgnoredWaitstate(ProcessRunnerImpl runner, HistoricActivityInstance instance, String duration) {
    super(runner, instance, duration);
  }

  @Override
  public void execute() {
    runner.setExecuted(historicDelegate.getId());
  }

  @Override
  protected ScenarioAction<IgnoredWaitstate> action(Scenario.Process scenario) {
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
  protected Object getDelegate() {
    return null;
  }

}
