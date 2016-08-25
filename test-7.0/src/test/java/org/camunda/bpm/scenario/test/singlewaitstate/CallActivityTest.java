package org.camunda.bpm.scenario.test.singlewaitstate;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.UserTaskAction;
import org.camunda.bpm.scenario.runner.UserTaskWaitstate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class CallActivityTest extends AbstractTest {

  @Mock
  protected Scenario.Process calledScenario;

  @Test
  @Deployment(resources = {
    "org/camunda/bpm/scenario/test/singlewaitstate/CallActivityTest.bpmn",
    "org/camunda/bpm/scenario/test/singlewaitstate/UserTaskTest.bpmn"
  })
  public void testCompleteCallActivityUserTask() {

    when(scenario.atCallActivity("CallActivity")).thenReturn(Scenario.use(calledScenario));
    when(calledScenario.atUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(UserTaskWaitstate task) {
        task.complete();
      }
    });

    Scenario.run(scenario).startBy("CallActivityTest").execute();

    verify(calledScenario, times(1)).hasFinished("UserTask");

    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(scenario, times(1)).hasFinished("CallActivity");

  }

  @Test
  @Deployment(resources = {
      "org/camunda/bpm/scenario/test/singlewaitstate/CallActivityTest.bpmn",
      "org/camunda/bpm/scenario/test/singlewaitstate/UserTaskTest.bpmn"
  })
  public void testDoNothingCallActivityUserTask() {

    when(scenario.atCallActivity("CallActivity")).thenReturn(Scenario.use(calledScenario));
    when(calledScenario.atUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(UserTaskWaitstate task) {
        // Deal with task but do nothing here
      }
    });

    Scenario.run(scenario).startBy("CallActivityTest").execute();

    verify(scenario, times(1)).hasStarted("CallActivity");
    verify(scenario, never()).hasFinished("CallActivity");
    verify(scenario, never()).hasFinished("EndEvent");

    verify(calledScenario, times(1)).hasStarted("UserTask");
    verify(calledScenario, never()).hasFinished("UserTask");

  }

  @Test(expected=AssertionError.class)
  @Deployment(resources = {
      "org/camunda/bpm/scenario/test/singlewaitstate/CallActivityTest.bpmn",
      "org/camunda/bpm/scenario/test/singlewaitstate/UserTaskTest.bpmn"
  })
  public void testDoNotDealWithCallActivity() {

    Scenario.run(scenario).startBy("CallActivityTest").execute();

  }

  @Test(expected=AssertionError.class)
  @Deployment(resources = {
      "org/camunda/bpm/scenario/test/singlewaitstate/CallActivityTest.bpmn",
      "org/camunda/bpm/scenario/test/singlewaitstate/UserTaskTest.bpmn"
  })
  public void testDoNotDealWithCallActivityUserTask() {

    when(scenario.atCallActivity("CallActivity")).thenReturn(Scenario.use(calledScenario));

    Scenario.run(scenario).startBy("CallActivityTest").execute();

  }

  @Test
  @Deployment(resources = {
      "org/camunda/bpm/scenario/test/singlewaitstate/CallActivityTest.bpmn",
      "org/camunda/bpm/scenario/test/singlewaitstate/UserTaskTest.bpmn"
  })
  public void testToBeforeCallActivity() {

    when(scenario.atCallActivity("CallActivity")).thenReturn(Scenario.use(calledScenario));

    Scenario.run(scenario).startBy("CallActivityTest").toBefore("CallActivity").execute();

    verify(scenario, times(1)).hasStarted("CallActivity");
    verify(scenario, never()).hasFinished("CallActivity");
    verify(scenario, never()).hasFinished("EndEvent");

    verify(calledScenario, never()).hasStarted("UserTask");

  }

  @Test
  @Deployment(resources = {
      "org/camunda/bpm/scenario/test/singlewaitstate/CallActivityTest.bpmn",
      "org/camunda/bpm/scenario/test/singlewaitstate/UserTaskTest.bpmn"
  })
  public void testToAfterCallActivity() {

    when(scenario.atCallActivity("CallActivity")).thenReturn(Scenario.use(calledScenario));
    when(calledScenario.atUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(UserTaskWaitstate task) {
        task.complete();
      }
    });

    Scenario.run(scenario).startBy("CallActivityTest").toAfter("CallActivity").execute();

    verify(scenario, times(1)).hasStarted("CallActivity");
    verify(scenario, times(1)).hasFinished("CallActivity");
    verify(scenario, times(1)).hasFinished("EndEvent");

    verify(calledScenario, times(1)).hasFinished("UserTask");

  }

  @Test
  @Deployment(resources = {
      "org/camunda/bpm/scenario/test/singlewaitstate/CallActivityTest.bpmn",
      "org/camunda/bpm/scenario/test/singlewaitstate/UserTaskTest.bpmn"
  })
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.atCallActivity("CallActivity")).thenReturn(Scenario.use(calledScenario));
    when(otherScenario.atCallActivity("CallActivity")).thenReturn(Scenario.use(calledScenario));
    when(calledScenario.atUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(UserTaskWaitstate task) {
        task.complete();
      }
    });

    Scenario.run(otherScenario).startBy("CallActivityTest").toBefore("CallActivity").execute();
    Scenario.run(scenario).startBy("CallActivityTest").execute();

    verify(scenario, times(1)).hasFinished("CallActivity");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, never()).hasFinished("CallActivity");

  }

}
