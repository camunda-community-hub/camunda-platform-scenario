package org.camunda.bpm.scenario;

import org.camunda.bpm.scenario.act.BusinessRuleTaskAction;
import org.camunda.bpm.scenario.act.EventBasedGatewayAction;
import org.camunda.bpm.scenario.act.MessageIntermediateCatchEventAction;
import org.camunda.bpm.scenario.act.MessageIntermediateThrowEventAction;
import org.camunda.bpm.scenario.act.ReceiveTaskAction;
import org.camunda.bpm.scenario.act.SendTaskAction;
import org.camunda.bpm.scenario.act.ServiceTaskAction;
import org.camunda.bpm.scenario.act.SignalIntermediateCatchEventAction;
import org.camunda.bpm.scenario.act.TimerIntermediateEventAction;
import org.camunda.bpm.scenario.act.UserTaskAction;
import org.camunda.bpm.scenario.run.Runnable;
import org.camunda.bpm.scenario.run.Runner;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ProcessScenario extends Runnable {

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
  Runner runsCallActivity(String activityId);

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
