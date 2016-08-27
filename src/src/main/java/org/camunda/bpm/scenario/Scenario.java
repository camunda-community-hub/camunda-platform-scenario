package org.camunda.bpm.scenario;

import org.camunda.bpm.scenario.action.EventBasedGatewayAction;
import org.camunda.bpm.scenario.action.MessageIntermediateCatchEventAction;
import org.camunda.bpm.scenario.action.MessageIntermediateThrowEventAction;
import org.camunda.bpm.scenario.action.ReceiveTaskAction;
import org.camunda.bpm.scenario.action.SendTaskAction;
import org.camunda.bpm.scenario.action.ServiceTaskAction;
import org.camunda.bpm.scenario.action.SignalIntermediateCatchEventAction;
import org.camunda.bpm.scenario.action.TimerIntermediateEventAction;
import org.camunda.bpm.scenario.action.UserTaskAction;
import org.camunda.bpm.scenario.impl.ProcessRunnerImpl;
import org.camunda.bpm.scenario.impl.ScenarioExecutorImpl;
import org.camunda.bpm.scenario.runner.ProcessRunner;
import org.camunda.bpm.scenario.runner.VerifiableScenario;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class Scenario {

  public static ProcessRunner.ProcessRunnerStartBy run(Process scenario) {
    return (ProcessRunner.ProcessRunnerStartBy) new ScenarioExecutorImpl(scenario).runners.get(0);
  }

  public static ProcessRunner.CallActivityRunner call(Process scenario) {
    return new ProcessRunnerImpl(null, scenario);
  }

  public interface Process extends VerifiableScenario {

    /**
     * @since Camunda BPM 7.0.0-Final
     */
    UserTaskAction actsOnUserTask(String activityId);

    /**
     * @since Camunda BPM 7.0.0-Final
     */
    TimerIntermediateEventAction actsOnTimerIntermediateEvent(String activityId);

    /**
     * @since Camunda BPM 7.0.0-Final
     */
    MessageIntermediateCatchEventAction actsOnMessageIntermediateCatchEvent(String activityId);

    /**
     * @since Camunda BPM 7.0.0-Final (as signallable execution only)
     * @since Camunda BPM 7.1.0-Final (as message event subscription)
     */
    ReceiveTaskAction actsOnReceiveTask(String activityId);

    /**
     * @since Camunda BPM 7.0.0-Final
     */
    SignalIntermediateCatchEventAction actsOnSignalIntermediateCatchEvent(String activityId);

    /**
     * @since Camunda BPM 7.0.0-Final
     */
    ProcessRunner.CallActivityRunner runsCallActivity(String activityId);

    /**
     * @since Camunda BPM 7.1.0-Final
     */
    EventBasedGatewayAction actsOnEventBasedGateway(String activityId);

    /**
     * @since Camunda BPM 7.4.0
     */
    ServiceTaskAction actsOnServiceTask(String activityId);

    /**
     * @since Camunda BPM 7.5.0
     */
    SendTaskAction actsOnSendTask(String activityId);

    /**
     * @since Camunda BPM 7.5.0
     */
    MessageIntermediateThrowEventAction actsOnMessageIntermediateThrowEvent(String activityId);

    /**
     * @since Camunda BPM 7.0.0
     */
    String waitsForActionOn(String activityId);

  }

}
