package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.impl.delegate.ProcessEngineServicesDelegateImpl;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class Savepoint<I> extends ProcessEngineServicesDelegateImpl {

  protected ProcessRunnerImpl runner;
  protected I runtimeDelegate;

  protected Savepoint(ProcessRunnerImpl runner) {
    super(runner.scenarioExecutor.processEngine);
    this.runner = runner;
  }

  public abstract String getExecutionId();

  protected abstract I getRuntimeDelegate();

  protected abstract void leave();

  public ProcessInstance getProcessInstance() {
    return runner.processInstance;
  };

}
