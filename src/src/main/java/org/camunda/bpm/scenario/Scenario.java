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

    /**
     * @since Camunda BPM 7.0.0-Final
     */
    UserTaskAction atUserTask(String activityId);

    /**
     * @since Camunda BPM 7.0.0-Final
     */
    TimerIntermediateCatchEventAction atTimerIntermediateCatchEvent(String activityId);

    /**
     * @since Camunda BPM 7.0.0-Final
     */
    MessageIntermediateCatchEventAction atMessageIntermediateCatchEvent(String activityId);

    /**
     * @since Camunda BPM 7.0.0-Final
     */
    ReceiveTaskAction atReceiveTask(String activityId);

    /**
     * @since Camunda BPM 7.0.0-Final
     */
    SignalIntermediateCatchEventAction atSignalIntermediateCatchEvent(String activityId);

    /**
     * @since Camunda BPM 7.4.0
     */
    ServiceTaskAction atServiceTask(String activityId);

    /**
     * @since Camunda BPM 7.5.0
     */
    SendTaskAction atSendTask(String activityId);

    /**
     * @since Camunda BPM 7.5.0
     */
    MessageIntermediateThrowEventAction atMessageIntermediateThrowEvent(String activityId);

    EventBasedGatewayAction atEventBasedGateway(String activityId);

    CallActivityRunner atCallActivity(String activityId);

    String needsTimeUntilFinishing(String activityId);

  }

}
