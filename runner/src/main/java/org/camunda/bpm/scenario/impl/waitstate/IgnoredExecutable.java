package org.camunda.bpm.scenario.impl.waitstate;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.act.Action;
import org.camunda.bpm.scenario.impl.ProcessInstanceRunner;
import org.camunda.bpm.scenario.impl.WaitstateExecutable;

import java.util.Date;

/**
 * @author Martin Schimak
 */
public class IgnoredExecutable extends WaitstateExecutable<Object> {

  public IgnoredExecutable(ProcessInstanceRunner runner, HistoricActivityInstance instance) {
    super(runner, instance);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void execute() {
    runner.setExecuted(this);
  }

  @Override
  protected Action<IgnoredExecutable> action(ProcessScenario scenario) {
    return null;
  }

  @Override
  protected Object getDelegate() {
    return null;
  }

  @Override
  public Date isExecutableAt() {
    return new Date(Long.MAX_VALUE);
  }

}
