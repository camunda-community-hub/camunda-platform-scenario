package org.camunda.bpm.scenario.runner;

import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
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

  protected static Waitstate newInstance(ProcessEngine processEngine, HistoricActivityInstance instance) {
    if ("callActivity".equals(instance.getActivityType())) {
      return new CallActivityWaitstate(processEngine, instance);
    } else if ("eventBasedGateway".equals(instance.getActivityType())) {
      return new EventBasedGatewayWaitstate(processEngine, instance);
    } else if ("serviceTask".equals(instance.getActivityType())) {
      return new ServiceTaskWaitstate(processEngine, instance);
    } else if ("sendTask".equals(instance.getActivityType())) {
      return new SendTaskWaitstate(processEngine, instance);
    } else if ("intermediateMessageThrow".equals(instance.getActivityType())) {
      return new MessageIntermediateThrowEventWaitstate(processEngine, instance);
    } else if ("intermediateMessageCatch".equals(instance.getActivityType())) {
      return new MessageIntermediateCatchEventWaitstate(processEngine, instance);
    } else if ("receiveTask".equals(instance.getActivityType())) {
      return new ReceiveTaskWaitstate(processEngine, instance);
    } else if ("intermediateSignalCatch".equals(instance.getActivityType())) {
      return new SignalIntermediateCatchEventWaitstate(processEngine, instance);
    } else if ("userTask".equals(instance.getActivityType())) {
      return new UserTaskWaitstate(processEngine, instance);
    } else if ("intermediateTimer".equals(instance.getActivityType())) {
      return new TimerIntermediateCatchEventWaitstate(processEngine, instance);
    } else {
      return new IgnoredWaitstate(processEngine, instance);
    }
  }

}
