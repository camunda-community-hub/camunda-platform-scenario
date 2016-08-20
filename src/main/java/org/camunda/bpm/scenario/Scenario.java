package org.camunda.bpm.scenario;


import org.camunda.bpm.scenario.runner.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class Scenario implements ScenarioHistory {

  public static ScenarioRunner run(String processDefinitionKey) {
    return ScenarioRunnerImpl.run(processDefinitionKey);
  }

  public static ScenarioRunner run(ScenarioStarter scenarioStarter) {
    return ScenarioRunnerImpl.run(scenarioStarter);
  }

  public abstract ScenarioAction<UserTaskWaitstate> atUserTask(String activityId);

  public abstract ScenarioAction<ServiceTaskWaitstate> atServiceTask(String activityId);

  public abstract ScenarioAction<SendTaskWaitstate> atSendTask(String activityId);

  public abstract ScenarioAction<MessageIntermediateThrowEventWaitstate> atMessageIntermediateThrowEvent(String activityId);

  public abstract ScenarioAction<TimerIntermediateCatchEventWaitstate> atTimerIntermediateCatchEvent(String activityId);

  public abstract ScenarioAction<MessageIntermediateCatchEventWaitstate> atMessageIntermediateCatchEvent(String activityId);

  public abstract ScenarioAction<ReceiveTaskWaitstate> atReceiveTask(String activityId);

  public abstract ScenarioAction<SignalIntermediateCatchEventWaitstate> atSignalIntermediateCatchEvent(String activityId);

  public abstract ScenarioAction<EventBasedGatewayWaitstate> atEventBasedGateway(String activityId);

  public abstract ScenarioAction<CallActivityWaitstate> atCallActivity(String activityId);

}
