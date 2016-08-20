package org.camunda.bpm.scenarios;


import org.camunda.bpm.scenarios.runner.ScenarioRunnerImpl;
import org.camunda.bpm.scenarios.waitstate.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class Scenario implements VerifiableScenario {

  public static ScenarioRunner run(String processDefinitionKey) {
    return new ScenarioRunnerImpl().running(processDefinitionKey);
  }

  public static ScenarioRunner run(ScenarioStarter scenarioStarter) {
    return new ScenarioRunnerImpl().running(scenarioStarter);
  }

  public abstract WaitstateAction<UserTaskWaitstate> atUserTask(String activityId);

  public abstract WaitstateAction<ServiceTaskWaitstate> atServiceTask(String activityId);

  public abstract WaitstateAction<SendTaskWaitstate> atSendTask(String activityId);

  public abstract WaitstateAction<MessageIntermediateThrowEventWaitstate> atMessageIntermediateThrowEvent(String activityId);

  public abstract WaitstateAction<TimerIntermediateCatchEventWaitstate> atTimerIntermediateCatchEvent(String activityId);

  public abstract WaitstateAction<MessageIntermediateCatchEventWaitstate> atMessageIntermediateCatchEvent(String activityId);

  public abstract WaitstateAction<ReceiveTaskWaitstate> atReceiveTask(String activityId);

  public abstract WaitstateAction<SignalIntermediateCatchEventWaitstate> atSignalIntermediateCatchEvent(String activityId);

  public abstract WaitstateAction<EventBasedGatewayWaitstate> atEventBasedGateway(String activityId);

  public abstract WaitstateAction<CallActivityWaitstate> atCallActivity(String activityId);

}
