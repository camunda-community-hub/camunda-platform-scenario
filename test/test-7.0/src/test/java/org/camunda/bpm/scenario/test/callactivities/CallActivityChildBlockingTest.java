package org.camunda.bpm.scenario.test.callactivities;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.act.UserTaskAction;
import org.camunda.bpm.scenario.delegate.TaskDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class CallActivityChildBlockingTest extends AbstractTest {

  @Mock
  ProcessScenario childScenario;

  @Mock
  ProcessScenario otherChildScenario;

  @Test
  @Deployment(resources = {
    "org/camunda/bpm/scenario/test/callactivities/CallActivityTest.bpmn",
    "org/camunda/bpm/scenario/test/callactivities/CallActivityChildBlockingTest.bpmn"
  })
  public void testCompleteCallActivity() {

    when(scenario.runsCallActivity("CallActivity")).thenReturn(Scenario.use(childScenario));

    when(childScenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    Scenario.run(scenario).startByKey("CallActivityTest").execute();

    verify(scenario, times(1)).hasFinished("CallActivity");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {
    "org/camunda/bpm/scenario/test/callactivities/CallActivityTest.bpmn",
    "org/camunda/bpm/scenario/test/callactivities/CallActivityChildBlockingTest.bpmn"
  })
  public void testDoNothing() {

    when(scenario.runsCallActivity("CallActivity")).thenReturn(Scenario.use(childScenario));

    when(childScenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        // Deal with task but do nothing here
      }
    });

    Scenario.run(scenario).startByKey("CallActivityTest").execute();

    verify(scenario, times(1)).hasStarted("CallActivity");
    verify(scenario, never()).hasFinished("CallActivity");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test(expected=AssertionError.class)
  @Deployment(resources = {
    "org/camunda/bpm/scenario/test/callactivities/CallActivityTest.bpmn",
    "org/camunda/bpm/scenario/test/callactivities/CallActivityChildBlockingTest.bpmn"
  })
  public void testDoNotDealWithCallActivity() {

    Scenario.run(scenario).startByKey("CallActivityTest").execute();

  }

  @Test
  @Deployment(resources = {
    "org/camunda/bpm/scenario/test/callactivities/CallActivityTest.bpmn",
    "org/camunda/bpm/scenario/test/callactivities/CallActivityChildBlockingTest.bpmn"
  })
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.runsCallActivity("CallActivity"))
        .thenReturn(Scenario.use(childScenario));
    when(otherScenario.runsCallActivity("CallActivity"))
        .thenReturn(Scenario.use(otherChildScenario));

    when(childScenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    when(otherChildScenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
      }
    });

    Scenario
      .run(scenario).startByKey("CallActivityTest")
      .run(otherScenario).startByKey("CallActivityTest")
      .execute();

    verify(scenario, times(1)).hasFinished("CallActivity");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, times(1)).hasStarted("CallActivity");
    verify(otherScenario, never()).hasFinished("CallActivity");

  }

}
