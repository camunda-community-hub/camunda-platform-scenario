package org.camunda.bpm.scenario.test.processes;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author Martin Schimak
 */
public class NonBlockingProcessTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/processes/NonBlockingProcessTest.bpmn"})
  public void testRun() {

    Scenario.run(scenario).startByKey("NonBlockingProcessTest").execute();

    verify(scenario, times(1)).hasFinished("SubProcess");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/processes/NonBlockingProcessTest.bpmn"})
  public void testWhileOtherProcessInstanceIsRunning() {

    Scenario
      .run(scenario).startByKey("NonBlockingProcessTest")
      .run(otherScenario).startByKey("NonBlockingProcessTest")
      .execute();

    verify(scenario, times(1)).hasFinished("SubProcess");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, times(1)).hasFinished("SubProcess");
    verify(otherScenario, times(1)).hasFinished("EndEvent");

  }

}
