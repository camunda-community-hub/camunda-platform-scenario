package org.camunda.bpm.scenario.test.escalations;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Ignore;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class EscalationEventSubProcessTriggeredTwiceConventionalTest extends AbstractTest {

  @Test @Ignore // In my mind should work, but does not work due to a Camunda NullpointerExecption
  @Deployment(resources = {"org/camunda/bpm/scenario/test/escalations/EscalationEventSubProcessTriggeredTwiceTest.bpmn"})
  public void testCompleteTask1First_Conventional() {

    ProcessInstance pi = rule.getRuntimeService()
        .startProcessInstanceByKey("EscalationEventSubProcessTriggeredTwiceTest");

    complete(task("UserTask1", pi));
    complete(task("UserTask2", pi));

    complete(task("UserTask2", pi));

    assertThat(pi)
        .hasPassed("EndEvent")
        .hasPassedInOrder("EndEventEscalated", "EndEventEscalated");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/escalations/EscalationEventSubProcessTriggeredTwiceTest.bpmn"})
  public void testCompleteTask2First_Conventional() {

    ProcessInstance pi = rule.getRuntimeService()
        .startProcessInstanceByKey("EscalationEventSubProcessTriggeredTwiceTest");

    complete(task("UserTask2", pi));
    complete(task("UserTask1", pi));

    complete(task("UserTask2", pi));

    assertThat(pi)
        .hasPassed("EndEvent")
        .hasPassedInOrder("EndEventEscalated", "EndEventEscalated");

  }

}
