package org.camunda.bpm.scenario.test.timers;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.act.TimerIntermediateEventAction;
import org.camunda.bpm.scenario.act.UserTaskAction;
import org.camunda.bpm.scenario.defer.Deferred;
import org.camunda.bpm.scenario.delegate.ProcessInstanceDelegate;
import org.camunda.bpm.scenario.delegate.TaskDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ParallelTimerIntermediateEventsTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/ParallelTimerIntermediateTest.bpmn"})
  public void testCompleteTaskImmediately() {

    when(scenario.waitsAtTimerIntermediateEvent("TimerIntermediateEventOne")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate timer) {
      }
    });

    when(scenario.waitsAtTimerIntermediateEvent("TimerIntermediateEventTwo")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate timer) {
      }
    });

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    Scenario executed = Scenario.run(scenario).startByKey("ParallelTimerIntermediateTest").execute();

    verify(scenario, times(1)).hasFinished("TimerIntermediateEventOne");
    verify(scenario, times(1)).hasFinished("TimerIntermediateEventTwo");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

    assertThat(executed.instance(scenario)).hasPassedInOrder("UserTask", "TimerIntermediateEventOne", "TimerIntermediateEventTwo");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/ParallelTimerIntermediateTest.bpmn"})
  public void testCompleteTaskAfterTwoMinutes() {

    when(scenario.waitsAtTimerIntermediateEvent("TimerIntermediateEventOne")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate timer) {
      }
    });

    when(scenario.waitsAtTimerIntermediateEvent("TimerIntermediateEventTwo")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate timer) {
      }
    });

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(final TaskDelegate task) {
        task.defer("PT2M", new Deferred() {
          @Override
          public void execute() {
            task.complete();
          }
        });
      }
    });

    Scenario executed = Scenario.run(scenario).startByKey("ParallelTimerIntermediateTest").execute();

    verify(scenario, times(1)).hasFinished("TimerIntermediateEventOne");
    verify(scenario, times(1)).hasFinished("TimerIntermediateEventTwo");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

    assertThat(executed.instance(scenario)).hasPassedInOrder("UserTask", "TimerIntermediateEventOne", "TimerIntermediateEventTwo");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/ParallelTimerIntermediateTest.bpmn"})
  public void testCompleteTaskAfterFiveMinutes() {

    when(scenario.waitsAtTimerIntermediateEvent("TimerIntermediateEventOne")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate timer) {
      }
    });

    when(scenario.waitsAtTimerIntermediateEvent("TimerIntermediateEventTwo")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate timer) {
      }
    });

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(final TaskDelegate task) {
        task.defer("PT5M", new Deferred() {
          @Override
          public void execute() {
            task.complete();
          }
        });
      }
    });

    Scenario executed = Scenario.run(scenario).startByKey("ParallelTimerIntermediateTest").execute();

    verify(scenario, times(1)).hasFinished("TimerIntermediateEventOne");
    verify(scenario, times(1)).hasFinished("TimerIntermediateEventTwo");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

    assertThat(executed.instance(scenario)).hasPassedInOrder("TimerIntermediateEventOne", "UserTask", "TimerIntermediateEventTwo");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/ParallelTimerIntermediateTest.bpmn"})
  public void testCompleteTaskAfterEightMinutes() {

    when(scenario.waitsAtTimerIntermediateEvent("TimerIntermediateEventOne")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate timer) {
      }
    });

    when(scenario.waitsAtTimerIntermediateEvent("TimerIntermediateEventTwo")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate timer) {
      }
    });

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(final TaskDelegate task) {
        task.defer("PT8M", new Deferred() {
          @Override
          public void execute() {
            task.complete();
          }
        });
      }
    });

    Scenario executed = Scenario.run(scenario).startByKey("ParallelTimerIntermediateTest").execute();

    verify(scenario, times(1)).hasFinished("TimerIntermediateEventOne");
    verify(scenario, times(1)).hasFinished("TimerIntermediateEventTwo");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

    assertThat(executed.instance(scenario)).hasPassedInOrder("TimerIntermediateEventOne", "TimerIntermediateEventTwo", "UserTask");

  }

  @Test(expected = Exception.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/ParallelTimerIntermediateTest.bpmn"})
  public void testDeferAnTimerIntermediateEventAction() {

    when(scenario.waitsAtTimerIntermediateEvent("TimerIntermediateEventOne")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate timer) {
        timer.defer("PT3M", new Deferred() {
          @Override
          public void execute() throws Exception {
            throw new Exception(); // expected
          }
        });
      }
    });

    when(scenario.waitsAtTimerIntermediateEvent("TimerIntermediateEventTwo")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate timer) {
      }
    });

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    Scenario.run(scenario).startByKey("ParallelTimerIntermediateTest").execute();

  }


  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/ParallelTimerIntermediateTest.bpmn"})
  public void testDeferAnTimerIntermediateEventActionForTooLong() {

    when(scenario.waitsAtTimerIntermediateEvent("TimerIntermediateEventOne")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate timer) {
        timer.defer("PT4M30S", new Deferred() {
          @Override
          public void execute() throws Exception {
            throw new Exception(); // not expected
          }
        });
      }
    });

    when(scenario.waitsAtTimerIntermediateEvent("TimerIntermediateEventTwo")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate timer) {
      }
    });

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    Scenario.run(scenario).startByKey("ParallelTimerIntermediateTest").execute();

  }

}
