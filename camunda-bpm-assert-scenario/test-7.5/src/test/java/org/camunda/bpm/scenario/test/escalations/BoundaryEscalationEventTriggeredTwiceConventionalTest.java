package org.camunda.bpm.scenario.test.escalations;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import java.util.List;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class BoundaryEscalationEventTriggeredTwiceConventionalTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/escalations/BoundaryEscalationEventTriggeredTwiceTest.bpmn"})
  public void testCompleteTask1First_Conventional() {

    ProcessInstance pi = rule.getRuntimeService()
        .startProcessInstanceByKey("BoundaryEscalationEventTriggeredTwiceTest");

    complete(task("UserTask1", pi));

    List<Task> tasks = taskQuery().processInstanceId(pi.getId()).taskDefinitionKey("UserTask2").list();
    complete(tasks.get(0)); // necessary as query returns two tasks at this point

    complete(task("UserTask2", pi));

    assertThat(pi)
        .hasPassed("EndEvent")
        .hasPassedInOrder("EndEventEscalated", "EndEventEscalated");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/escalations/BoundaryEscalationEventTriggeredTwiceTest.bpmn"})
  public void testCompleteTask2First_Conventional() {

    ProcessInstance pi = rule.getRuntimeService()
        .startProcessInstanceByKey("BoundaryEscalationEventTriggeredTwiceTest");

    complete(task("UserTask2", pi));
    complete(task("UserTask1", pi));

    complete(task("UserTask2", pi));

    assertThat(pi)
        .hasPassed("EndEvent")
        .hasPassedInOrder("EndEventEscalated", "EndEventEscalated");

  }

}
