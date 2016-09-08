package org.camunda.bpm.scenario.test.combinations;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.act.UserTaskAction;
import org.camunda.bpm.scenario.delegate.TaskDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class UserTaskAndMockedServiceTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/combinations/UserTaskAndMockedServiceTest.bpmn"})
  public void testCompleteTask() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(final TaskDelegate task) throws Exception {
        task.complete();
      }
    });

    Scenario.run(scenario).startByKey("UserTaskAndMockedServiceTest").execute();

    verify(scenario, times(1)).hasFinished("EndEvent");

  }

}
