package org.camunda.bpm.scenario;


import org.camunda.bpm.scenario.action.CallActivityAction;
import org.camunda.bpm.scenario.action.EventBasedGatewayAction;
import org.camunda.bpm.scenario.action.MessageIntermediateCatchEventAction;
import org.camunda.bpm.scenario.action.MessageIntermediateThrowEventAction;
import org.camunda.bpm.scenario.action.ReceiveTaskAction;
import org.camunda.bpm.scenario.action.SendTaskAction;
import org.camunda.bpm.scenario.action.ServiceTaskAction;
import org.camunda.bpm.scenario.action.SignalIntermediateCatchEventAction;
import org.camunda.bpm.scenario.action.TimerIntermediateCatchEventAction;
import org.camunda.bpm.scenario.action.UserTaskAction;
import org.camunda.bpm.scenario.runner.ScenarioRunnerImpl;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class Scenario implements ScenarioHistory {

  public static ScenarioRunner process(String processDefinitionKey) {
    return ScenarioRunnerImpl.start(processDefinitionKey);
  }

  public static ScenarioRunner process(ScenarioStarter scenarioStarter) {
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
