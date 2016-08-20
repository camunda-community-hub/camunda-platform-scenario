package org.camunda.bpm.scenario;


import org.camunda.bpm.scenario.action.*;
import org.camunda.bpm.scenario.runner.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class Scenario implements ScenarioHistory {

  public static ScenarioRunner run(String processDefinitionKey) {
    return ScenarioRunnerImpl.start(processDefinitionKey);
  }

  public static ScenarioRunner run(ScenarioStarter scenarioStarter) {
    return ScenarioRunnerImpl.start(scenarioStarter);
  }

  public abstract UserTaskAction atUserTask(String activityId);

  public abstract ServiceTaskAction atServiceTask(String activityId);

  public abstract SendTaskAction atSendTask(String activityId);

  public abstract MessageIntermediateThrowEventAction atMessageIntermediateThrowEvent(String activityId);

  public abstract TimerIntermediateCatchEventAction atTimerIntermediateCatchEvent(String activityId);

  public abstract MessageIntermediateCatchEventAction atMessageIntermediateCatchEvent(String activityId);

  public abstract ReceiveTaskAction atReceiveTask(String activityId);

  public abstract SignalIntermediateCatchEventAction atSignalIntermediateCatchEvent(String activityId);

  public abstract EventBasedGatewayAction atEventBasedGateway(String activityId);

  public abstract CallActivityAction atCallActivity(String activityId);

}
