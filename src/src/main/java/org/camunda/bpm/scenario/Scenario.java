package org.camunda.bpm.scenario;

import org.camunda.bpm.scenario.action.BusinessRuleTaskAction;
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

  public static ProcessRunner.ToBeStartedBy run(Process scenario) {
    return new ScenarioExecutorImpl(scenario).toBeStartedBy();
  }

  public static ProcessRunner use(Process scenario) {
    return new ProcessRunnerImpl(null, scenario);
  }

  public interface Process extends VerifiableScenario {

    /**
     * @since Camunda BPM 7.0.0-Final
     */
    UserTaskAction waitsAtUserTask(String activityId);

    /**
     * @since Camunda BPM 7.0.0-Final
     */
    TimerIntermediateEventAction waitsAtTimerIntermediateEvent(String activityId);

    /**
     * @since Camunda BPM 7.0.0-Final
     */
    MessageIntermediateCatchEventAction waitsAtMessageIntermediateCatchEvent(String activityId);

    /**
     * @since Camunda BPM 7.0.0-Final (as signallable execution only)
     * @since Camunda BPM 7.1.0-Final (as message event subscription)
     */
    ReceiveTaskAction waitsAtReceiveTask(String activityId);

    /**
     * @since Camunda BPM 7.0.0-Final
     */
    SignalIntermediateCatchEventAction waitsAtSignalIntermediateCatchEvent(String activityId);

    /**
     * @since Camunda BPM 7.0.0-Final
     */
    ProcessRunner runsCallActivity(String activityId);

    /**
     * @since Camunda BPM 7.1.0-Final
     */
    EventBasedGatewayAction waitsAtEventBasedGateway(String activityId);

    /**
     * @since Camunda BPM 7.4.0
     */
    ServiceTaskAction waitsAtServiceTask(String activityId);

    /**
     * @since Camunda BPM 7.5.0
     */
    SendTaskAction waitsAtSendTask(String activityId);

    /**
     * @since Camunda BPM 7.5.0
     */
    MessageIntermediateThrowEventAction waitsAtMessageIntermediateThrowEvent(String activityId);

    /**
     * @since Camunda BPM 7.5.0
     */
    BusinessRuleTaskAction waitsAtBusinessRuleTask(String activityId);

  }

}
