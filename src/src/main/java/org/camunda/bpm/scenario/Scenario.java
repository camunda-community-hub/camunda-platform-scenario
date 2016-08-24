package org.camunda.bpm.scenario;

import org.camunda.bpm.scenario.action.EventBasedGatewayAction;
import org.camunda.bpm.scenario.action.MessageIntermediateCatchEventAction;
import org.camunda.bpm.scenario.action.MessageIntermediateThrowEventAction;
import org.camunda.bpm.scenario.action.ReceiveTaskAction;
import org.camunda.bpm.scenario.action.SendTaskAction;
import org.camunda.bpm.scenario.action.ServiceTaskAction;
import org.camunda.bpm.scenario.action.SignalIntermediateCatchEventAction;
import org.camunda.bpm.scenario.action.TimerIntermediateCatchEventAction;
import org.camunda.bpm.scenario.action.UserTaskAction;
import org.camunda.bpm.scenario.runner.CallActivityRunner;
import org.camunda.bpm.scenario.runner.ScenarioHistory;
import org.camunda.bpm.scenario.runner.ProcessRunner;
import org.camunda.bpm.scenario.runner.ScenarioExecutor;
import org.camunda.bpm.scenario.runner.ProcessRunnerImpl;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class Scenario {

  public static ProcessRunner run(Process scenario) {
    return (ProcessRunner) new ScenarioExecutor(scenario).runners.get(0);
  }

  public static CallActivityRunner use(Process scenario) {
    return new ProcessRunnerImpl(null, scenario);
  }

  public interface Process extends ScenarioHistory {

    UserTaskAction atUserTask(String activityId);

    ServiceTaskAction atServiceTask(String activityId);

    SendTaskAction atSendTask(String activityId);

    MessageIntermediateThrowEventAction atMessageIntermediateThrowEvent(String activityId);

    TimerIntermediateCatchEventAction atTimerIntermediateCatchEvent(String activityId);

    MessageIntermediateCatchEventAction atMessageIntermediateCatchEvent(String activityId);

    ReceiveTaskAction atReceiveTask(String activityId);

    SignalIntermediateCatchEventAction atSignalIntermediateCatchEvent(String activityId);

    EventBasedGatewayAction atEventBasedGateway(String activityId);

    CallActivityRunner atCallActivity(String activityId);

    String needsTimeUntilFinishing(String activityId);

  }

}
