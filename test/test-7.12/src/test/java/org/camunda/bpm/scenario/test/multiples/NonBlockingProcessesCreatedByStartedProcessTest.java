package org.camunda.bpm.scenario.test.multiples;

import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.report.junit.ProcessEngineRuleWithReporting;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.camunda.bpm.engine.test.mock.Mocks.register;
import static org.camunda.bpm.scenario.Scenario.run;
import static org.camunda.bpm.scenario.Scenario.use;
import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak
 */
public class NonBlockingProcessesCreatedByStartedProcessTest {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRuleWithReporting();

  public ProcessScenario process;

  @Before
  public void defaults() {

    process = mock(ProcessScenario.class);
    ProcessScenario otherProcess = mock(ProcessScenario.class);
    ProcessScenario anotherProcess = mock(ProcessScenario.class);

    when(process.waitsAtUserTask("UserTask")).thenReturn(task -> task.complete());
    when(process.waitsAtReceiveTask("ReceiveTask")).thenReturn(delegate -> {});
    when(process.runsProcessInstance("OtherProcess")).thenReturn(use(otherProcess));
    when(otherProcess.runsProcessInstance("AnotherProcess")).thenReturn(use(anotherProcess));

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/multiples/NonBlockingProcessesCreatedByStartedProcess.bpmn"})
  public void testProcessCreatedByStartedProcess() {

    register("createOtherProcess", (JavaDelegate) execution -> rule.getRuntimeService().correlateMessage("otherMessage"));
    register("createAnotherProcess", (JavaDelegate) execution -> rule.getRuntimeService().correlateMessage("anotherMessage"));
    register("correlateWithProcess", (JavaDelegate) execution -> rule.getRuntimeService().correlateMessage("message"));

    run(process).startByKey("Process").execute();

    verify(process).hasCompleted("EndEvent");

  }

}
