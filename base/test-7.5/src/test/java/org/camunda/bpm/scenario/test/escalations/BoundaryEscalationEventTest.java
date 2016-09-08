package org.camunda.bpm.scenario.test.escalations;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.act.UserTaskAction;
import org.camunda.bpm.scenario.delegate.TaskDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class BoundaryEscalationEventTest extends AbstractTest {

  void complete(final Map<String, Object> variables) {
    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete(variables);
      }
    });
  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/escalations/BoundaryEscalationEventTest.bpmn"})
  public void testCompleteTask() {

    complete(withVariables("escalate", false));

    Scenario.run(scenario).startByKey("BoundaryEscalationEventTest").execute();

    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventInterrupted");
    verify(scenario, never()).hasFinished("EndEventNotInterrupted");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/escalations/BoundaryEscalationEventTest.bpmn"})
  public void testEscalateNonInterrupting() {

    complete(withVariables("escalate", true, "interrupt", false));

    Scenario.run(scenario).startByKey("BoundaryEscalationEventTest").execute();

    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventInterrupted");
    verify(scenario, times(1)).hasFinished("EndEventNotInterrupted");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/escalations/BoundaryEscalationEventTest.bpmn"})
  public void testEscalateInterrupting() {

    complete(withVariables("escalate", true, "interrupt", true));

    Scenario.run(scenario).startByKey("BoundaryEscalationEventTest").execute();

    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, never()).hasFinished("EndEventCompleted");
    verify(scenario, times(1)).hasFinished("EndEventInterrupted");
    verify(scenario, never()).hasFinished("EndEventNotInterrupted");

  }

}
