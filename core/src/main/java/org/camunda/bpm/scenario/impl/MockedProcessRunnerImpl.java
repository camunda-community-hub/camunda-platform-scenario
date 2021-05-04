package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.act.BusinessRuleTaskAction;
import org.camunda.bpm.scenario.act.ConditionalIntermediateEventAction;
import org.camunda.bpm.scenario.act.EventBasedGatewayAction;
import org.camunda.bpm.scenario.act.MessageEndEventAction;
import org.camunda.bpm.scenario.act.MessageIntermediateCatchEventAction;
import org.camunda.bpm.scenario.act.MessageIntermediateThrowEventAction;
import org.camunda.bpm.scenario.act.MockedCallActivityAction;
import org.camunda.bpm.scenario.act.ReceiveTaskAction;
import org.camunda.bpm.scenario.act.SendTaskAction;
import org.camunda.bpm.scenario.act.ServiceTaskAction;
import org.camunda.bpm.scenario.act.SignalIntermediateCatchEventAction;
import org.camunda.bpm.scenario.act.TimerIntermediateEventAction;
import org.camunda.bpm.scenario.act.UserTaskAction;
import org.camunda.bpm.scenario.run.Runner;

/**
 * @author <a href="martin.schimak@plexiti.com">Martin Schimak</a>
 */
public class MockedProcessRunnerImpl extends ProcessRunnerImpl {

  public MockedProcessRunnerImpl(final MockedCallActivityAction action) {

    super(null, new ProcessScenario() {

      @Override
      public UserTaskAction waitsAtUserTask(String activityId) {
        throw new IllegalStateException();
      }

      @Override
      public TimerIntermediateEventAction waitsAtTimerIntermediateEvent(String activityId) {
        throw new IllegalStateException();
      }

      @Override
      public MessageIntermediateCatchEventAction waitsAtMessageIntermediateCatchEvent(String activityId) {
        throw new IllegalStateException();
      }

      @Override
      public ReceiveTaskAction waitsAtReceiveTask(String activityId) {
        throw new IllegalStateException();
      }

      @Override
      public SignalIntermediateCatchEventAction waitsAtSignalIntermediateCatchEvent(String activityId) {
        throw new IllegalStateException();
      }

      @Override
      public Runner runsCallActivity(String activityId) {
        throw new IllegalStateException();
      }

      @Override
      public MockedCallActivityAction waitsAtMockedCallActivity(String activityId) {
        throw new IllegalStateException();
      }

      @Override
      public EventBasedGatewayAction waitsAtEventBasedGateway(String activityId) {
        throw new IllegalStateException();
      }

      @Override
      public ServiceTaskAction waitsAtServiceTask(String activityId) {
        return action;
      }

      @Override
      public SendTaskAction waitsAtSendTask(String activityId) {
        throw new IllegalStateException();
      }

      @Override
      public MessageIntermediateThrowEventAction waitsAtMessageIntermediateThrowEvent(String activityId) {
        throw new IllegalStateException();
      }

      @Override
      public MessageEndEventAction waitsAtMessageEndEvent(String activityId) {
        throw new IllegalStateException();
      }

      @Override
      public BusinessRuleTaskAction waitsAtBusinessRuleTask(String activityId) {
        throw new IllegalStateException();
      }

      @Override
      public ConditionalIntermediateEventAction waitsAtConditionalIntermediateEvent(String activityId) {
        throw new IllegalStateException();
      }

      @Override
      public void hasStarted(String activityId) {
        throw new IllegalStateException();
      }

      @Override
      public void hasFinished(String activityId) {
        throw new IllegalStateException();
      }

      @Override
      public void hasCompleted(String activityId) {
        throw new IllegalStateException();
      }

      @Override
      public void hasCanceled(String activityId) {
        throw new IllegalStateException();
      }

    });

  }

  @Override
  public void setExecuted() { }

}
