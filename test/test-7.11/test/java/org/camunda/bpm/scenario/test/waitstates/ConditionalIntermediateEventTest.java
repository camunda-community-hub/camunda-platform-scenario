package org.camunda.bpm.scenario.test.waitstates;

import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.act.ConditionalIntermediateEventAction;
import org.camunda.bpm.scenario.defer.Deferred;
import org.camunda.bpm.scenario.delegate.ProcessInstanceDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
@Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/ConditionalIntermediateEventTest.bpmn"})
public class ConditionalIntermediateEventTest extends AbstractTest {

  @Before
  public void setCondition() {
    variables.put("condition", false);
  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/ConditionalIntermediateEventTest.bpmn"})
  public void testTriggerCondition() {

    when(scenario.waitsAtConditionalIntermediateEvent("ConditionalIntermediateEvent")).thenReturn(new ConditionalIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate processInstance) {
        Execution execution = rule.getRuntimeService().createExecutionQuery().processInstanceId(processInstance.getId()).activityId("ConditionalIntermediateEvent").singleResult();
        rule.getRuntimeService().signal(execution.getId()); // not encouraged for conditional events, but possible and tested here...
      }
    });

    Scenario.run(scenario).startByKey("ConditionalIntermediateEventTest", variables).execute();

    verify(scenario, times(1)).hasFinished("ConditionalIntermediateEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/ConditionalIntermediateEventTest.bpmn"})
  public void testConditionInitiallyFalseAndDoNothing() {

    when(scenario.waitsAtConditionalIntermediateEvent("ConditionalIntermediateEvent")).thenReturn(new ConditionalIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate processInstance) {
        // Deal with processInstance but do nothing here
      }
    });

    Scenario.run(scenario)
        .startByKey("ConditionalIntermediateEventTest", variables)
        .execute();

    verify(scenario, never()).hasFinished("ConditionalIntermediateEvent");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/ConditionalIntermediateEventTest.bpmn"})
  public void testConditionInitiallyFalseAndSetTrue() {

    when(scenario.waitsAtConditionalIntermediateEvent("ConditionalIntermediateEvent")).thenReturn(new ConditionalIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate processInstance) {
        rule.getRuntimeService().setVariable(processInstance.getId(), "condition", true);
      }
    });

    Scenario.run(scenario)
        .startByKey("ConditionalIntermediateEventTest", variables)
        .execute();

    verify(scenario, times(1)).hasFinished("ConditionalIntermediateEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/ConditionalIntermediateEventTest.bpmn"})
  public void testConditionInitiallyFalseAndDoNotDealWithEvent() {

    Scenario.run(scenario).startByKey("ConditionalIntermediateEventTest", variables).execute();

    verify(scenario, never()).hasFinished("ConditionalIntermediateEvent");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/ConditionalIntermediateEventTest.bpmn"})
  public void testConditionInitiallyTrueAndDoNothing() {

    when(scenario.waitsAtConditionalIntermediateEvent("ConditionalIntermediateEvent")).thenReturn(new ConditionalIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate processInstance) {
        // Deal with processInstance but do nothing here
      }
    });

    variables.put("condition", true);

    Scenario.run(scenario)
        .startByKey("ConditionalIntermediateEventTest", variables)
        .execute();

    verify(scenario, times(1)).hasFinished("ConditionalIntermediateEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/ConditionalIntermediateEventTest.bpmn"})
  public void testConditionInitiallyTrueAndDoNotDealWithEvent() {

    variables.put("condition", true);

    Scenario.run(scenario).startByKey("ConditionalIntermediateEventTest", variables).execute();

    verify(scenario, times(1)).hasFinished("ConditionalIntermediateEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test(expected = Exception.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/ConditionalIntermediateEventTest.bpmn"})
  public void testDoSomethingDeferred() {

    when(scenario.waitsAtConditionalIntermediateEvent("ConditionalIntermediateEvent")).thenReturn(new ConditionalIntermediateEventAction() {
      @Override
      public void execute(final ProcessInstanceDelegate pi) {
        pi.defer("PT3M", new Deferred() {
          @Override
          public void execute() throws Exception {
            throw new Exception(); // expected
          }
        });
      }
    });

    Scenario.run(scenario).startByKey("ConditionalIntermediateEventTest").execute();

  }

}
