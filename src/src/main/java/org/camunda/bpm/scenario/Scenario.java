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
import org.camunda.bpm.scenario.runner.ScenarioRunner;
import org.camunda.bpm.scenario.runner.ScenarioRunnerImpl;
import org.camunda.bpm.scenario.runner.ScenarioStarter;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class Scenario {

  public static ScenarioRunner process(String processDefinitionKey) {
    return ScenarioRunnerImpl.start(processDefinitionKey);
  }

  public static ScenarioRunner process(ScenarioStarter scenarioStarter) {
    return ScenarioRunnerImpl.start(scenarioStarter);
  }

  public interface Bpmn extends ScenarioHistory {

    UserTaskAction atUserTask(String activityId);

    ServiceTaskAction atServiceTask(String activityId);

    SendTaskAction atSendTask(String activityId);

    MessageIntermediateThrowEventAction atMessageIntermediateThrowEvent(String activityId);

    TimerIntermediateCatchEventAction atTimerIntermediateCatchEvent(String activityId);

    MessageIntermediateCatchEventAction atMessageIntermediateCatchEvent(String activityId);

    ReceiveTaskAction atReceiveTask(String activityId);

    SignalIntermediateCatchEventAction atSignalIntermediateCatchEvent(String activityId);

    EventBasedGatewayAction atEventBasedGateway(String activityId);

    CallActivityAction atCallActivity(String activityId);

  }

}
