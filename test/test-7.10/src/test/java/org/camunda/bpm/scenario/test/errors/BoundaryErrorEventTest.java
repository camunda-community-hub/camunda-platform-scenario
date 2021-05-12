package org.camunda.bpm.scenario.test.errors;

import org.assertj.core.api.Assertions;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.act.ServiceTaskAction;
import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.processEngine;
import static org.mockito.Mockito.*;

/**
 * @author <a href="martin.schimak@plexiti.com">Martin Schimak</a>
 */
@Deployment(resources = {"org/camunda/bpm/scenario/test/errors/BoundaryErrorEventTest.bpmn"})
public class BoundaryErrorEventTest extends AbstractTest {

  @Test
  public void testHandleBpmnErrorWithVariables() {

    when(scenario.waitsAtServiceTask("ServiceTask")).thenReturn(new ServiceTaskAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("key", "value");
        externalTask.handleBpmnError("errorCode", variables);
      }
    });

    ProcessInstance pi = Scenario
       .run(scenario)
       .startByKey("BoundaryErrorEventTest")
       .execute()
       .instance(scenario);

    verify(scenario, never()).hasFinished("EndEventCompleted");
    verify(scenario, times(1)).hasFinished("EndEventError");

    HistoricVariableInstance hvi = processEngine().getHistoryService().createHistoricVariableInstanceQuery()
       .processInstanceId(pi.getId())
       .variableName("key")
       .singleResult();
    Assertions.assertThat(hvi.getValue()).isEqualTo("value");

  }

}
