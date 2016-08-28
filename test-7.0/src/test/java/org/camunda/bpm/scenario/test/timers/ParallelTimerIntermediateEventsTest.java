package org.camunda.bpm.scenario.test.timers;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.TimerIntermediateEventAction;
import org.camunda.bpm.scenario.action.UserTaskAction;
import org.camunda.bpm.scenario.delegate.TaskDelegate;
import org.camunda.bpm.scenario.delegate.ProcessInstanceDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ParallelTimerIntermediateEventsTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/ParallelTimerIntermediateTest.bpmn"})
  public void testCompleteTaskImmediately() {

    when(scenario.actsOnTimerIntermediateEvent("TimerIntermediateEventOne")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate timer) {
      }
    });

    when(scenario.actsOnTimerIntermediateEvent("TimerIntermediateEventTwo")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate timer) {
      }
    });

    when(scenario.actsOnUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    ProcessInstance pi = Scenario.run(scenario).startByKey("ParallelTimerIntermediateTest").execute().getProcessInstance();

    verify(scenario, times(1)).hasFinished("TimerIntermediateEventOne");
    verify(scenario, times(1)).hasFinished("TimerIntermediateEventTwo");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

    assertThat(pi).hasPassedInOrder("UserTask", "TimerIntermediateEventOne", "TimerIntermediateEventTwo");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/ParallelTimerIntermediateTest.bpmn"})
  public void testCompleteTaskAfterTwoMinutes() {

    when(scenario.actsOnTimerIntermediateEvent("TimerIntermediateEventOne")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate timer) {
      }
    });

    when(scenario.actsOnTimerIntermediateEvent("TimerIntermediateEventTwo")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate timer) {
      }
    });

    when(scenario.waitsForActionOn("UserTask")).thenReturn("PT2M");

    when(scenario.actsOnUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    ProcessInstance pi = Scenario.run(scenario).startByKey("ParallelTimerIntermediateTest").execute().getProcessInstance();

    verify(scenario, times(1)).hasFinished("TimerIntermediateEventOne");
    verify(scenario, times(1)).hasFinished("TimerIntermediateEventTwo");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

    assertThat(pi).hasPassedInOrder("UserTask", "TimerIntermediateEventOne", "TimerIntermediateEventTwo");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/ParallelTimerIntermediateTest.bpmn"})
  public void testCompleteTaskAfterFiveMinutes() {

    when(scenario.actsOnTimerIntermediateEvent("TimerIntermediateEventOne")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate timer) {
      }
    });

    when(scenario.actsOnTimerIntermediateEvent("TimerIntermediateEventTwo")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate timer) {
      }
    });

    when(scenario.waitsForActionOn("UserTask")).thenReturn("PT5M");

    when(scenario.actsOnUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    ProcessInstance pi = Scenario.run(scenario).startByKey("ParallelTimerIntermediateTest").execute().getProcessInstance();

    verify(scenario, times(1)).hasFinished("TimerIntermediateEventOne");
    verify(scenario, times(1)).hasFinished("TimerIntermediateEventTwo");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

    assertThat(pi).hasPassedInOrder("TimerIntermediateEventOne", "UserTask", "TimerIntermediateEventTwo");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/ParallelTimerIntermediateTest.bpmn"})
  public void testCompleteTaskAfterEightMinutes() {

    when(scenario.actsOnTimerIntermediateEvent("TimerIntermediateEventOne")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate timer) {
      }
    });

    when(scenario.actsOnTimerIntermediateEvent("TimerIntermediateEventTwo")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate timer) {
      }
    });

    when(scenario.waitsForActionOn("UserTask")).thenReturn("PT8M");

    when(scenario.actsOnUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    ProcessInstance pi = Scenario.run(scenario).startByKey("ParallelTimerIntermediateTest").execute().getProcessInstance();

    verify(scenario, times(1)).hasFinished("TimerIntermediateEventOne");
    verify(scenario, times(1)).hasFinished("TimerIntermediateEventTwo");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

    assertThat(pi).hasPassedInOrder("TimerIntermediateEventOne", "TimerIntermediateEventTwo", "UserTask");

  }

}
