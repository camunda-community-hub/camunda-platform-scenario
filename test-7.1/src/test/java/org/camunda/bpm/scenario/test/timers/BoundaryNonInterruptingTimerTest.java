package org.camunda.bpm.scenario.test.timers;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.act.UserTaskAction;
import org.camunda.bpm.scenario.defer.Deferred;
import org.camunda.bpm.scenario.delegate.TaskDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class BoundaryNonInterruptingTimerTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/BoundaryNonInterruptingTimerTest.bpmn"})
  public void testCompleteTask() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    Scenario.run(scenario).startByKey("BoundaryNonInterruptingTimerTest").execute();

    verify(scenario, times(1)).waitsAtUserTask("UserTask");
    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventAdditional");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/BoundaryNonInterruptingTimerTest.bpmn"})
  public void testExactlyReachingMaximalTimeForTask() {

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

    Scenario.run(scenario).startByKey("BoundaryNonInterruptingTimerTest").execute();

    verify(scenario, times(1)).waitsAtUserTask("UserTask");
    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventAdditional");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/BoundaryNonInterruptingTimerTest.bpmn"})
  public void testTakeMuchTooLongForTask() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(final TaskDelegate task) {
        task.defer("PT6M", new Deferred() {
          @Override
          public void execute() {
            task.complete();
          }
        });
      }
    });

    Scenario.run(scenario).startByKey("BoundaryNonInterruptingTimerTest").execute();

    verify(scenario, times(1)).waitsAtUserTask("UserTask");
    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, times(1)).hasFinished("EndEventAdditional");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/BoundaryNonInterruptingTimerTest.bpmn"})
  public void testTakeABitTimeForTask() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(final TaskDelegate task) {
        task.defer("PT4M", new Deferred() {
          @Override
          public void execute() {
            task.complete();
          }
        });
      }
    });

    Scenario.run(scenario).startByKey("BoundaryNonInterruptingTimerTest").execute();

    verify(scenario, times(1)).waitsAtUserTask("UserTask");
    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventAdditional");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/BoundaryNonInterruptingTimerTest.bpmn"})
  public void testDoNothing() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        // Deal with task but do nothing here
      }
    });

    Scenario.run(scenario).startByKey("BoundaryNonInterruptingTimerTest").execute();

    verify(scenario, times(1)).waitsAtUserTask("UserTask");
    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, never()).hasFinished("UserTask");
    verify(scenario, never()).hasFinished("EndEventCompleted");
    verify(scenario, times(1)).hasFinished("EndEventAdditional");

  }

  @Test(expected=AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/BoundaryNonInterruptingTimerTest.bpmn"})
  public void testDoNotDealWithTask() {

    Scenario.run(scenario).startByKey("BoundaryNonInterruptingTimerTest").execute();

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/timers/BoundaryNonInterruptingTimerTest.bpmn"})
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    when(otherScenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
      }
    });

    Scenario.run(otherScenario).startByKey("BoundaryNonInterruptingTimerTest").execute();
    Scenario.run(scenario).startByKey("BoundaryNonInterruptingTimerTest").execute();

    verify(scenario, times(1)).waitsAtUserTask("UserTask");
    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventAdditional");

  }

}
