package org.camunda.bpm.scenario.runner;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.impl.calendar.DurationHelper;
import org.camunda.bpm.engine.impl.util.ClockUtil;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;
import org.camunda.bpm.engine.runtime.SignalEventReceivedBuilder;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.ScenarioAction;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

  protected static Waitstate newInstance(ProcessEngine engine, HistoricActivityInstance instance, String duration) {
    if (classNames.containsKey(instance.getActivityType())) {
      try {
        return (Waitstate) Class.forName(Waitstate.class.getPackage().getName() + "." + classNames.get(instance.getActivityType())).getConstructor(ProcessEngine.class, HistoricActivityInstance.class, String.class).newInstance(engine, instance, duration);
      } catch (Exception e) {
        throw new IllegalArgumentException(e);
      }
    }
    return new IgnoredWaitstate(engine, instance, duration);
  }

  protected HistoricActivityInstance historicDelegate;
  protected String duration;

  protected Waitstate(ProcessEngine processEngine, HistoricActivityInstance instance, String duration) {
    super(processEngine);
    this.historicDelegate = instance;
    this.runtimeDelegate = getRuntimeDelegate();
    this.duration = duration;
  }

  @Override
  public String getExecutionId() {
    return historicDelegate.getExecutionId();
  }

  public String getActivityId() {
    return historicDelegate.getActivityId();
  }

  protected void execute(Scenario.Process scenario) {
    ScenarioAction action = action(scenario);
    if (action == null)
      throw new AssertionError("Process Instance {"
          + getProcessInstance().getProcessDefinitionId() + ", "
          + getProcessInstance().getProcessInstanceId() + "} "
          + "waits at an unexpected " + getClass().getSimpleName().substring(0, getClass().getSimpleName().length() - 9)
          + " '" + historicDelegate.getActivityId() +"'.");
    action.execute(this);
  }

  protected abstract ScenarioAction action(Scenario.Process scenario);

  protected abstract void leave(Map<String, Object> variables);

  public SignalEventReceivedBuilder createSignal(String signalName) {
    return getRuntimeService().createSignalEvent(signalName);
  }

  public MessageCorrelationBuilder createMessage(String messageName) {
    return getRuntimeService().createMessageCorrelation(messageName);
  }

  protected boolean isSelf(Job timer) {
    return false;
  }

  protected Date getEndTime() {
    Date endTime = historicDelegate.getStartTime();
    if (duration != null) {
      try {
        if (duration == null || !duration.startsWith("P")) {
          throw new IllegalArgumentException("Provided argument '" + duration + "' is not a duration expression.");
        }
        Date now = ClockUtil.getCurrentTime();
        ClockUtil.setCurrentTime(historicDelegate.getStartTime());
        DurationHelper durationHelper = new DurationHelper(duration);
        endTime = durationHelper.getDateAfter();
        ClockUtil.setCurrentTime(now);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return endTime;
  }

}
