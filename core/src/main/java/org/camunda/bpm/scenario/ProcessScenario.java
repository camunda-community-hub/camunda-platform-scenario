package org.camunda.bpm.scenario;

import org.camunda.bpm.scenario.act.BusinessRuleTaskAction;
import org.camunda.bpm.scenario.act.ConditionalIntermediateEventAction;
import org.camunda.bpm.scenario.act.EventBasedGatewayAction;
import org.camunda.bpm.scenario.act.MessageEndEventAction;
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
   * By implementing - or stubbing/mocking - this method, you
   * define what should be done when the process reaches the
   * user task (waitstate) with the activity id provided.
   *
   * @param activityId the activity id of the user task reached.
   * @return action to be executed when process reaches the user
   * task (waitstate) with the activity id provided.
   *
   * @since Camunda BPM 7.0.0-Final
   */
  UserTaskAction waitsAtUserTask(String activityId);

  /**
   * By implementing - or stubbing/mocking - this method, you
   * define what should be done when the process reaches the
   * timer intermediate event (waitstate) with the activity id
   * provided.
   *
   * @param activityId the activity id of the timer intermediate
   * event reached.
   * @return action to be executed when process reaches the timer
   * intermediate event (waitstate) with the activity id provided.
   *
   * @since Camunda BPM 7.0.0-Final
   */
  TimerIntermediateEventAction waitsAtTimerIntermediateEvent(String activityId);

  /**
   * By implementing - or stubbing/mocking - this method, you
   * define what should be done when the process reaches the
   * message intermediate catch event (waitstate) with the
   * activity id provided.
   *
   * @param activityId the activity id of the message intermediate
   * catch event reached.
   * @return action to be executed when process reaches the message
   * intermediate catch event (waitstate) with the activity id
   * provided.
   *
   * @since Camunda BPM 7.0.0-Final
   */
  MessageIntermediateCatchEventAction waitsAtMessageIntermediateCatchEvent(String activityId);

  /**
   * By implementing - or stubbing/mocking - this method, you
   * define what should be done when the process reaches the
   * receive task (waitstate) with the activity id provided.
   *
   * @param activityId the activity id of the receive task reached.
   * @return action to be executed when process reaches the receive
   * task (waitstate) with the activity id provided.
   *
   * @since Camunda BPM 7.1.0-Final (as message event subscription)
   */
  ReceiveTaskAction waitsAtReceiveTask(String activityId);

  /**
   * By implementing - or stubbing/mocking - this method, you
   * define what should be done when the process reaches the
   * signal intermediate catch event (waitstate) with the
   * activity id provided.
   *
   * @param activityId the activity id of the signal intermediate
   * catch event reached.
   * @return action to be executed when process reaches the signal
   * intermediate catch event (waitstate) with the activity id
   * provided.
   *
   * @since Camunda BPM 7.0.0-Final
   */
  SignalIntermediateCatchEventAction waitsAtSignalIntermediateCatchEvent(String activityId);

  /**
   * By implementing - or stubbing/mocking - this method, you
   * define what should be done when the process runs the
   * call activity with the activity id provided.
   *
   * @param activityId the activity id of the call activity.
   * @return scenario runner to be executed when process runs
   * the call activity with the activity id provided.
   *
   * @since Camunda BPM 7.0.0-Final
   */
  Runner runsCallActivity(String activityId);

  /**
   * By implementing - or stubbing/mocking - this method, you
   * define what should be done when the process reaches the
   * event based gateway (waitstate) with the activity id provided.
   *
   * @param activityId the activity id of the event based gateway
   * reached.
   * @return action to be executed when process reaches the event
   * based gateway (waitstate) with the activity id provided.
   *
   * @since Camunda BPM 7.1.0-Final
   */
  EventBasedGatewayAction waitsAtEventBasedGateway(String activityId);

  /**
   * By implementing - or stubbing/mocking - this method, you
   * define what should be done when the process reaches the
   * service task (waitstate) with the activity id provided.
   * Note that a service task is only a waitstate in case you
   * implement it by means of a Camunda BPM ExternalTask.
   *
   * @param activityId the activity id of the send task
   * (external task) reached.
   * @return action to be executed when process reaches the
   * service task (external task waitstate) with the activity
   * id provided.
   *
   * @since Camunda BPM 7.4.0
   */
  ServiceTaskAction waitsAtServiceTask(String activityId);

  /**
   * By implementing - or stubbing/mocking - this method, you
   * define what should be done when the process reaches the
   * send task (waitstate) with the activity id provided.
   * Note that a send task is only a waitstate in case you
   * implement it by means of a Camunda BPM ExternalTask.
   *
   * @param activityId the activity id of the send task
   * (external task) reached.
   * @return action to be executed when process reaches the
   * service task (external task waitstate) with the activity
   * id provided.
   *
   * @since Camunda BPM 7.5.0
   */
  SendTaskAction waitsAtSendTask(String activityId);

  /**
   * By implementing - or stubbing/mocking - this method, you
   * define what should be done when the process reaches the
   * message intermediate throw event (waitstate) with the
   * activity id provided. Note that a message intermediate
   * throw event is only a waitstate in case you implement
   * it by means of a Camunda BPM ExternalTask.
   *
   * @param activityId the activity id of the message intermediate
   * throw event (external task) reached.
   * @return action to be executed when process reaches the message
   * intermediate throw event (external task waitstate) with the
   * activity id provided.
   *
   * @since Camunda BPM 7.5.0
   */
  MessageIntermediateThrowEventAction waitsAtMessageIntermediateThrowEvent(String activityId);

  /**
   * By implementing - or stubbing/mocking - this method, you
   * define what should be done when the process reaches the
   * message end event (waitstate) with the activity id provided.
   * Note that a message intermediate throw event is only a
   * waitstate in case you implement it by means of a Camunda BPM
   * ExternalTask.
   *
   * @param activityId the activity id of the message end event
   * (external task) reached.
   * @return action to be executed when process reaches the message
   * end event (external task waitstate) with the activity id provided.
   *
   * @since Camunda BPM 7.5.0
   */
  MessageEndEventAction waitsAtMessageEndEvent(String activityId);

  /**
   * By implementing - or stubbing/mocking - this method, you
   * define what should be done when the process reaches the
   * business rule task (waitstate) with the activity id provided.
   * Note that a business rule task is only a waitstate in case you
   * implement it by means of a Camunda BPM ExternalTask.
   *
   * @param activityId the activity id of the business rule task
   * (external task) reached.
   * @return action to be executed when process reaches the
   * business rule task (external task waitstate) with the activity
   * id provided.
   *
   * @since Camunda BPM 7.5.0
   */
  BusinessRuleTaskAction waitsAtBusinessRuleTask(String activityId);

  /**
   * By implementing - or stubbing/mocking - this method, you
   * define what should be done when the process reaches the
   * conditional intermediate event (waitstate) with the activity id
   * provided.
   *
   * @param activityId the activity id of the conditional intermediate
   * event reached.
   * @return action to be executed when process reaches the conditional
   * intermediate event (waitstate) with the activity id provided.
   *
   * @since Camunda BPM 7.6.0
   */
  ConditionalIntermediateEventAction waitsAtConditionalIntermediateEvent(String activityId);

}
