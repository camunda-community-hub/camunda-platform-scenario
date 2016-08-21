package org.camunda.bpm.scenario.runner;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.delegate.ProcessEngineServicesDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class Savepoint<I> extends ProcessEngineServicesDelegate {

  protected I runtimeDelegate;

  protected Savepoint(ProcessEngine processEngine) {
    super(processEngine);
  }

  public abstract String getExecutionId();

  protected abstract I getRuntimeDelegate();

  protected abstract void leave();

  public ProcessInstance getProcessInstance() {
    Execution execution = getRuntimeService().createExecutionQuery().executionId(getExecutionId()).singleResult();
    return getRuntimeService().createProcessInstanceQuery().processInstanceId(execution.getProcessInstanceId()).singleResult();
  };

}
