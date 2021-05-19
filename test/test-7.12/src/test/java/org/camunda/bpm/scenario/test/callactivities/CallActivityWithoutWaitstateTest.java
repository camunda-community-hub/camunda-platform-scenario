package org.camunda.bpm.scenario.test.callactivities;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.report.junit.ProcessEngineRuleWithReporting;
import org.junit.Rule;
import org.junit.Test;

import static org.camunda.bpm.scenario.Scenario.run;
import static org.camunda.bpm.scenario.Scenario.use;
import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak
 */
public class CallActivityWithoutWaitstateTest {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRuleWithReporting();

  @Test
  @Deployment(resources = {
    "org/camunda/bpm/scenario/test/callactivities/CallActivityWithoutWaitstateTest.bpmn",
    "org/camunda/bpm/scenario/test/callactivities/ChildWithoutWaitstate.bpmn"
  })
  public void testCompleteCallActivity() {

    ProcessScenario parent = mock(ProcessScenario.class);
    ProcessScenario child = mock(ProcessScenario.class);
    when(parent.runsCallActivity("CallActivity")).thenReturn(use(child));

    run(parent).startByKey("CallActivityWithoutWaitstate").execute();

    verify(parent, times(1)).hasFinished("CallActivity");
    verify(parent, times(1)).hasFinished("EndEvent");
    verify(child, times(1)).hasFinished("ChildEndEvent");

  }

}
