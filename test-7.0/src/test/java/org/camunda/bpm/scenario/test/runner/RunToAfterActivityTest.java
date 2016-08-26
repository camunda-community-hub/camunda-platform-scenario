package org.camunda.bpm.scenario.test.runner;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.UserTaskAction;
import org.camunda.bpm.scenario.delegate.TaskDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class RunToAfterActivityTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/runner/RunToAfterActivityTest.bpmn"})
  public void testRunToAfterTaskOneAndTwo() {

    when(scenario.actsOnUserTask(anyString())).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    Scenario.run(scenario).startBy("RunToAfterActivityTest")
      .toAfter("UserTaskOne", "UserTaskTwo")
      .execute();

    verify(scenario, times(2)).actsOnUserTask(anyString());
    verify(scenario, times(1)).hasFinished("UserTaskOne");
    verify(scenario, times(1)).hasFinished("UserTaskTwo");
    verify(scenario, never()).hasFinished("UserTaskThree");
    verify(scenario, never()).hasFinished("UserTaskFour");
    verify(scenario, never()).hasFinished("EndEvent");

  }

}
