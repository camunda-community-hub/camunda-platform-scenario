package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.impl.delegate.AbstractProcessEngineServicesDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class AbstractSavepoint<I> extends AbstractProcessEngineServicesDelegate {

  protected ProcessRunnerImpl runner;
  protected I runtimeDelegate;

  protected AbstractSavepoint(ProcessRunnerImpl runner) {
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
