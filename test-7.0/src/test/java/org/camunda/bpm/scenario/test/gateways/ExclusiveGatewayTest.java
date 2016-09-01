package org.camunda.bpm.scenario.test.gateways;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.act.UserTaskAction;
import org.camunda.bpm.scenario.delegate.TaskDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ExclusiveGatewayTest extends AbstractTest {

  @Before public void setVariable() {
    variables.put("one", true);
  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/gateways/ExclusiveGatewayTest.bpmn"})
  public void testCompleteTaskOne() {

    when(scenario.waitsAtUserTask("UserTaskOne")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    when(scenario.waitsAtUserTask("UserTaskTwo")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    Scenario.run(scenario).startByKey("ExclusiveGatewayTest", variables).execute();

    verify(scenario, times(1)).hasStarted("UserTaskOne");
    verify(scenario, times(1)).hasFinished("UserTaskOne");
    verify(scenario, never()).hasStarted("UserTaskTwo");
    verify(scenario, never()).waitsAtUserTask("UserTaskTwo");
    verify(scenario, never()).hasFinished("UserTaskTwo");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/gateways/ExclusiveGatewayTest.bpmn"})
  public void testCompleteTaskTwo() {

    when(scenario.waitsAtUserTask("UserTaskOne")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    when(scenario.waitsAtUserTask("UserTaskTwo")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    variables.put("one", false);

    Scenario.run(scenario).startByKey("ExclusiveGatewayTest", variables).execute();

    verify(scenario, never()).hasStarted("UserTaskOne");
    verify(scenario, never()).hasFinished("UserTaskOne");
    verify(scenario, times(1)).hasStarted("UserTaskTwo");
    verify(scenario, times(1)).waitsAtUserTask("UserTaskTwo");
    verify(scenario, times(1)).hasFinished("UserTaskTwo");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/gateways/ExclusiveGatewayTest.bpmn"})
  public void testDoNothingOnBothTasks() {

    when(scenario.waitsAtUserTask("UserTaskOne")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {

      }
    });

    when(scenario.waitsAtUserTask("UserTaskTwo")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {

      }
    });

    Scenario.run(scenario).startByKey("ExclusiveGatewayTest", variables).execute();

    verify(scenario, times(1)).hasStarted("UserTaskOne");
    verify(scenario, never()).hasFinished("UserTaskOne");
    verify(scenario, never()).hasStarted("UserTaskTwo");
    verify(scenario, never()).hasFinished("UserTaskTwo");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/gateways/ExclusiveGatewayTest.bpmn"})
  public void testDoNothingOnOneTask() {

    when(scenario.waitsAtUserTask("UserTaskOne")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    when(scenario.waitsAtUserTask("UserTaskTwo")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {

      }
    });

    Scenario.run(scenario).startByKey("ExclusiveGatewayTest", variables).execute();

    verify(scenario, times(1)).hasStarted("UserTaskOne");
    verify(scenario, times(1)).hasFinished("UserTaskOne");
    verify(scenario, never()).hasStarted("UserTaskTwo");
    verify(scenario, never()).hasFinished("UserTaskTwo");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test(expected=AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/gateways/ExclusiveGatewayTest.bpmn"})
  public void testDoNotDealWithTask() {

    when(scenario.waitsAtUserTask("UserTaskTwo")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    Scenario.run(scenario).startByKey("ExclusiveGatewayTest", variables).execute();

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/gateways/ExclusiveGatewayTest.bpmn"})
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.waitsAtUserTask("UserTaskOne")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    when(otherScenario.waitsAtUserTask("UserTaskOne")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
      }
    });

    when(scenario.waitsAtUserTask("UserTaskTwo")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    Scenario.run(otherScenario).startByKey("ExclusiveGatewayTest",variables).execute();
    Scenario.run(scenario).startByKey("ExclusiveGatewayTest", variables).execute();

    verify(scenario, times(1)).hasFinished("UserTaskOne");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, never()).hasFinished("UserTaskOne");
    verify(otherScenario, never()).hasFinished("EndEvent");

  }

}
