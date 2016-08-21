package org.camunda.bpm.scenario.runner;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;
import org.camunda.bpm.engine.runtime.SignalEventReceivedBuilder;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.ScenarioAction;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class Waitstate<I> extends Savepoint<I> {

  private static Map<String, String> classNames = new HashMap<String, String>(); static {
    classNames.put("userTask", "UserTaskWaitstate");
    classNames.put("intermediateSignalCatch", "SignalIntermediateCatchEventWaitstate");
    classNames.put("intermediateMessageCatch", "MessageIntermediateCatchEventWaitstate");
    classNames.put("receiveTask", "ReceiveTaskWaitstate");
    classNames.put("intermediateTimer", "TimerIntermediateCatchEventWaitstate");
    classNames.put("eventBasedGateway", "EventBasedGatewayWaitstate");
    classNames.put("callActivity", "CallActivityWaitstate");
    classNames.put("serviceTask", "ServiceTaskWaitstate");
    classNames.put("sendTask", "SendTaskWaitstate");
    classNames.put("intermediateMessageThrow", "MessageIntermediateThrowEventWaitstate");
  }

  protected static Waitstate newInstance(ProcessEngine engine, HistoricActivityInstance instance) {
    if (classNames.containsKey(instance.getActivityType())) {
      try {
        return (Waitstate) Class.forName(Waitstate.class.getPackage().getName() + "." + classNames.get(instance.getActivityType())).getConstructor(ProcessEngine.class, HistoricActivityInstance.class).newInstance(engine, instance);
      } catch (Exception e) {
        throw new IllegalArgumentException(e);
      }
    }
    return new IgnoredWaitstate(engine, instance);
  }

  protected HistoricActivityInstance historicDelegate;

  protected Waitstate(ProcessEngine processEngine, HistoricActivityInstance instance) {
    super(processEngine);
    this.historicDelegate = instance;
    this.runtimeDelegate = getRuntimeDelegate();
  }

  @Override
  public String getExecutionId() {
    return historicDelegate.getExecutionId();
  }

  public String getActivityId() {
    return historicDelegate.getActivityId();
  }

  protected void execute(Scenario scenario) {
    ScenarioAction action = action(scenario);
    if (action == null)
      throw new AssertionError("Process Instance {"
          + getProcessInstance().getProcessDefinitionId() + ", "
          + getProcessInstance().getProcessInstanceId() + "} "
          + "waits at an unexpected " + getClass().getSimpleName().substring(0, getClass().getSimpleName().length() - 9)
          + " '" + historicDelegate.getActivityId() +"'.");
    action.execute(this);
  }

  protected abstract ScenarioAction action(Scenario scenario);

  protected abstract void leave(Map<String, Object> variables);

  protected boolean unfinished() {
    return getHistoryService().createHistoricActivityInstanceQuery().activityInstanceId(historicDelegate.getId()).unfinished().singleResult() != null;
  }

  public SignalEventReceivedBuilder createSignal(String signalName) {
    return getRuntimeService().createSignalEvent(signalName);
  }

  public MessageCorrelationBuilder createMessage(String messageName) {
    return getRuntimeService().createMessageCorrelation(messageName);
  }

  public void triggerTimer(String activityId) {
    Job job = getManagementService().createJobQuery().processInstanceId(getProcessInstance().getId()).activityId(activityId).timers().singleResult();
    getManagementService().executeJob(job.getId());
  }

}
