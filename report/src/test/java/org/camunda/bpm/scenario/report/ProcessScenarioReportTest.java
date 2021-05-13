package org.camunda.bpm.scenario.report;

import org.assertj.core.api.Assertions;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.report.junit.ProcessScenarioRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;
import static org.camunda.bpm.scenario.Scenario.run;
import static org.camunda.bpm.scenario.Scenario.use;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Martin Schimak
 */

@Deployment(resources = {
  "org/camunda/bpm/scenario/report/InsuranceApplication.bpmn",
  "org/camunda/bpm/scenario/report/DocumentRequest.bpmn",
  "org/camunda/bpm/scenario/report/RiskCheck.dmn"
})
public class ProcessScenarioReportTest {

  private final ProcessScenario insuranceApplication = mock(ProcessScenario.class);
  private final ProcessScenario documentRequest = mock(ProcessScenario.class);
  @Rule
  public ProcessEngineRule rule = new ProcessScenarioRule();
  private Map<String, Object> variables;

  @Before
  public void setupDefaultScenario() {

    variables = Variables.createVariables()
      .putValue("applicantAge", "30")
      .putValue("carManufacturer", "VW")
      .putValue("carType", "Golf");

    when(insuranceApplication.waitsAtUserTask("UserTaskDecideAboutApplication")).thenReturn((task) -> {
      task.complete(withVariables("approved", true));
    });

    when(insuranceApplication.waitsAtUserTask("UserTaskCheckApplicationUnderwriter")).thenReturn((task) -> {
      assertThat(task).hasCandidateGroup("underwriter");
      task.complete();
    });

    when(insuranceApplication.waitsAtUserTask("UserTaskCheckApplicationTeamlead")).thenReturn((task) -> {
      assertThat(task).hasCandidateGroup("teamlead");
      task.complete();
    });

    when(insuranceApplication.waitsAtUserTask("UserTaskSpeedUpManualCheck")).thenReturn((task) -> {
      assertThat(task).hasCandidateGroup("management");
      assertThat(task.getProcessInstance()).isNotEnded();
      task.complete();
    });

    when(insuranceApplication.waitsAtSendTask("SendTaskSendPolicy")).thenReturn((externalTask) -> {
      assertThat(externalTask).hasTopicName("SendMail");
      externalTask.complete();
    });

    when(insuranceApplication.waitsAtSendTask("SendTaskSendRejection")).thenReturn((externalTask) -> {
      assertThat(externalTask).hasTopicName("SendMail");
      externalTask.complete();
    });

    when(insuranceApplication.runsCallActivity("CallActivityDocumentRequest"))
      .thenReturn(use(documentRequest));

    when(documentRequest.waitsAtSendTask("SendTaskRequestDocuments")).thenReturn((externalTask) -> {
      assertThat(externalTask).hasTopicName("SendMail");
      externalTask.complete();
    });

    when(documentRequest.waitsAtReceiveTask("ReceiveTaskWaitForDocuments")).thenReturn((receiveTask) -> {
      Assertions.assertThat(receiveTask.getEventType()).isEqualTo("message");
      Assertions.assertThat(receiveTask.getEventName()).isEqualTo("MSG_DOCUMENT_RECEIVED");
      receiveTask.receive();
    });

    when(documentRequest.waitsAtUserTask("UserTaskCallCustomer")).thenReturn((task) -> {
      task.complete();
    });

    when(documentRequest.waitsAtSendTask("SendTaskSendReminder")).thenReturn((externalTask) -> {
      assertThat(externalTask).hasTopicName("SendMail");
      externalTask.complete();
    });

  }

  @Test
  public void testGreenScenario() {

    variables.put("riskAssessment", "green");
    run(insuranceApplication).startByKey("InsuranceApplication", variables).execute();

  }

  @Test
  public void testRedScenario() {

    variables = Variables.createVariables()
      .putValue("applicantAge", 20)
      .putValue("carManufacturer", "Porsche")
      .putValue("carType", "911");

    run(insuranceApplication).startByKey("InsuranceApplication", variables).execute();

  }

  @Test
  public void testManualApprovalScenario() {

    variables = Variables.createVariables()
      .putValue("applicantAge", 30)
      .putValue("carManufacturer", "Porsche")
      .putValue("carType", "911");

    run(insuranceApplication).startByKey("InsuranceApplication", variables).execute();

  }

  @Test
  public void testManualNoActionScenario() {

    variables = Variables.createVariables()
      .putValue("applicantAge", 30)
      .putValue("carManufacturer", "Porsche")
      .putValue("carType", "911");

    when(insuranceApplication.waitsAtUserTask("UserTaskDecideAboutApplication")).thenReturn((task) -> {
      task.defer("P1Y", () -> {
      });
    });

    run(insuranceApplication).startByKey("InsuranceApplication", variables).execute();

  }

  @Test
  public void testManualRejectionScenario() {

    variables = Variables.createVariables()
      .putValue("applicantAge", 30)
      .putValue("carManufacturer", "Porsche")
      .putValue("carType", "911");

    when(insuranceApplication.waitsAtUserTask("UserTaskDecideAboutApplication")).thenReturn((task) -> {
      task.complete(withVariables("approved", false));
    });

    run(insuranceApplication).startByKey("InsuranceApplication", variables).execute();

  }

  @Test
  public void testDocumentRequestScenario() {

    variables = Variables.createVariables()
      .putValue("applicantAge", 30)
      .putValue("carManufacturer", "Porsche")
      .putValue("carType", "911");

    when(insuranceApplication.waitsAtUserTask("UserTaskDecideAboutApplication")).thenReturn((task) -> {
      runtimeService().correlateMessage("msgDocumentNecessary");
      task.complete(withVariables("approved", true));
    });

    run(insuranceApplication).startByKey("InsuranceApplication", variables).execute();

  }

  @Test
  public void testDocumentRequestBitLateScenario() {

    variables = Variables.createVariables()
      .putValue("applicantAge", 30)
      .putValue("carManufacturer", "Porsche")
      .putValue("carType", "911");

    when(insuranceApplication.waitsAtUserTask("UserTaskDecideAboutApplication")).thenReturn((task) -> {
      runtimeService().correlateMessage("msgDocumentNecessary");
      task.complete(withVariables("approved", true));
    });

    when(documentRequest.waitsAtReceiveTask("ReceiveTaskWaitForDocuments")).thenReturn((receiveTask) -> {
      receiveTask.defer("P1DT1M", receiveTask::receive);
    });

    run(insuranceApplication).startByKey("InsuranceApplication", variables).execute();

  }

  @Test
  public void testDocumentRequestVeryLateScenario() {

    variables = Variables.createVariables()
      .putValue("applicantAge", 30)
      .putValue("carManufacturer", "Porsche")
      .putValue("carType", "911");

    when(insuranceApplication.waitsAtUserTask("UserTaskDecideAboutApplication")).thenReturn((task) -> {
      runtimeService().correlateMessage("msgDocumentNecessary");
      task.complete(withVariables("approved", true));
    });

    when(documentRequest.waitsAtReceiveTask("ReceiveTaskWaitForDocuments")).thenReturn((receiveTask) -> {
      receiveTask.defer("P7DT1M", receiveTask::receive);
    });

    run(insuranceApplication).startByKey("InsuranceApplication", variables).execute();

  }

}
