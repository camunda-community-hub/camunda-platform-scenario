package org.camunda.bpm.scenario.test.escalations;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.act.UserTaskAction;
import org.camunda.bpm.scenario.defer.Deferred;
import org.camunda.bpm.scenario.delegate.TaskDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Ignore;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class EscalationEventSubProcessTriggeredTwiceTest extends AbstractTest {

  @Test @Ignore // In my mind should work, but does not work due to a Camunda NullpointerExecption
  @Deployment(resources = {"org/camunda/bpm/scenario/test/escalations/EscalationEventSubProcessTriggeredTwiceTest.bpmn"})
  public void testCompleteTask1First() {

    when(scenario.waitsAtUserTask("UserTask1")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) throws Exception {
        task.complete();
      }
    });

    when(scenario.waitsAtUserTask("UserTask2")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(final TaskDelegate task) throws Exception {
        task.defer("PT1M", new Deferred() {
          @Override
          public void execute() throws Exception {
            task.complete();
          }
        });
      }
    });

    Scenario.run(scenario).startByKey("EscalationEventSubProcessTriggeredTwiceTest").execute();

    verify(scenario, times(2)).hasFinished("EndEventEscalated");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/escalations/EscalationEventSubProcessTriggeredTwiceTest.bpmn"})
  public void testCompleteTask2First() {

    when(scenario.waitsAtUserTask("UserTask1")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(final TaskDelegate task) throws Exception {
        task.defer("PT1M", new Deferred() {
          @Override
          public void execute() throws Exception {
            task.complete();
          }
        });
      }
    });

    when(scenario.waitsAtUserTask("UserTask2")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) throws Exception {
        task.complete();
      }
    });

    Scenario.run(scenario).startByKey("EscalationEventSubProcessTriggeredTwiceTest").execute();

    verify(scenario, times(2)).hasFinished("EndEventEscalated");

  }

}
