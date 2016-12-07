package org.camunda.bpm.scenario.examples.insuranceapplication;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.Scenario;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
@Deployment(resources = {
  "org/camunda/bpm/scenario/examples/insuranceapplication/InsuranceApplication.bpmn",
  "org/camunda/bpm/scenario/examples/insuranceapplication/DocumentRequest.bpmn",
  "org/camunda/bpm/scenario/examples/insuranceapplication/RiskCheck.dmn"
})
public class InsuranceApplicationProcessTest {

  @Rule
  @ClassRule
  public static ProcessEngineRule rule =
      TestCoverageProcessEngineRuleBuilder.create()
        .withDetailedCoverageLogging().build();

  // Mock all waitstates in main process and call activity with a scenario
  @Mock private ProcessScenario insuranceApplication;
  @Mock private ProcessScenario documentRequest;
  private Map<String, Object> variables;

  // Setup a default behaviour for all "completable" waitstates in your
  // processes. You might want to override the behaviour in test methods.
  @Before
  public void setupDefaultScenario() {

    MockitoAnnotations.initMocks(this);

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
      assertThat(externalTask.getTopicName()).isEqualTo("SendMail");
      externalTask.complete();
    });

    when(insuranceApplication.waitsAtSendTask("SendTaskSendRejection")).thenReturn((externalTask) -> {
      assertThat(externalTask.getTopicName()).isEqualTo("SendMail");
      externalTask.complete();
    });

    when(insuranceApplication.runsCallActivity("CallActivityDocumentRequest"))
      .thenReturn(Scenario.use(documentRequest));

    when(documentRequest.waitsAtSendTask("SendTaskRequestDocuments")).thenReturn((externalTask) -> {
      assertThat(externalTask.getTopicName()).isEqualTo("SendMail");
      externalTask.complete();
    });

    when(documentRequest.waitsAtReceiveTask("ReceiveTaskWaitForDocuments")).thenReturn((receiveTask) -> {
      assertThat(receiveTask.getEventType()).isEqualTo("message");
      assertThat(receiveTask.getEventName()).isEqualTo("MSG_DOCUMENT_RECEIVED");
      receiveTask.receive();
    });

    when(documentRequest.waitsAtUserTask("UserTaskCallCustomer")).thenReturn((task) -> {
      task.complete();
    });

    when(documentRequest.waitsAtSendTask("SendTaskSendReminder")).thenReturn((externalTask) -> {
      assertThat(externalTask.getTopicName()).isEqualTo("SendMail");
      externalTask.complete();
    });

  }

  @Test
  public void testGreenScenario() {

    variables.put("riskAssessment", "green");

    Scenario scenario = Scenario.run(insuranceApplication)
        .startByKey("InsuranceApplication", variables) // either just start process by key ...
        .execute();

    assertThat(scenario.instance(insuranceApplication)).variables().containsEntry("riskAssessment", "green");
    verify(insuranceApplication, never()).hasStarted("SubProcessManualCheck");
    verify(insuranceApplication).hasFinished("EndEventApplicationAccepted");

  }

  @Test
  public void testYellowScenario() {

    variables = Variables.createVariables()
      .putValue("applicantAge", 30)
      .putValue("carManufacturer", "Porsche")
      .putValue("carType", "911");

    Scenario scenario = Scenario.run(insuranceApplication)
      .startBy(() -> { // ... or define your own starter function
        return rule.getRuntimeService().startProcessInstanceByKey("InsuranceApplication", variables);
      })
      .execute();

    assertThat(scenario.instance(insuranceApplication)).variables().containsEntry("riskAssessment", "yellow");
    verify(insuranceApplication).hasCompleted("SubProcessManualCheck");
    verify(insuranceApplication).hasFinished("EndEventApplicationAccepted");

  }

  @Test
  public void testRedScenario() {

    variables = Variables.createVariables()
      .putValue("applicantAge", 20)
      .putValue("carManufacturer", "Porsche")
      .putValue("carType", "911");

    Scenario scenario = Scenario.run(insuranceApplication)
        .startByKey("InsuranceApplication", variables)
        .execute();

    assertThat(scenario.instance(insuranceApplication)).variables().containsEntry("riskAssessment", "red");

    verify(insuranceApplication, never()).hasStarted("SubProcessManualCheck");
    verify(insuranceApplication).hasFinished("EndEventApplicationRejected");

  }

  @Test
  public void testManualApprovalScenario() {

    variables = Variables.createVariables()
      .putValue("applicantAge", 30)
      .putValue("carManufacturer", "Porsche")
      .putValue("carType", "911");

    Scenario scenario = Scenario.run(insuranceApplication)
        .startByKey("InsuranceApplication", variables)
        .execute();

    assertThat(scenario.instance(insuranceApplication)).variables()
      .containsEntry("riskAssessment", "yellow")
      .containsEntry("approved", true);

    verify(insuranceApplication).hasCompleted("SubProcessManualCheck");
    verify(insuranceApplication).hasFinished("EndEventApplicationAccepted");

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

    Scenario scenario = Scenario.run(insuranceApplication)
        .startByKey("InsuranceApplication", variables)
        .execute();

    assertThat(scenario.instance(insuranceApplication)).variables()
      .containsEntry("riskAssessment", "yellow")
      .containsEntry("approved", false);

    verify(insuranceApplication).hasCompleted("SubProcessManualCheck");
    verify(insuranceApplication).hasFinished("EndEventApplicationRejected");

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

    Scenario.run(insuranceApplication)
        .startByKey("InsuranceApplication", variables)
        .execute();

    verify(insuranceApplication).hasCompleted("CallActivityDocumentRequest");

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

    Scenario.run(insuranceApplication)
        .startByKey("InsuranceApplication", variables)
        .execute();

    verify(insuranceApplication).hasCompleted("CallActivityDocumentRequest");
    verify(insuranceApplication, never()).hasStarted("UserTaskSpeedUpManualCheck");
    verify(documentRequest).hasCompleted("SendTaskSendReminder");

    verify(documentRequest, times(1)).waitsAtReceiveTask("ReceiveTaskWaitForDocuments");

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

    Scenario.run(insuranceApplication)
        .startByKey("InsuranceApplication", variables)
        .execute();

    verify(insuranceApplication, times(1)).hasStarted("UserTaskSpeedUpManualCheck");
    verify(insuranceApplication).hasCompleted("EndEventApplicationAccepted");

    verify(documentRequest, times(1)).hasCompleted("UserTaskCallCustomer");
    verify(documentRequest, times(5)).hasCompleted("SendTaskSendReminder");
    verify(documentRequest).hasCanceled("ReceiveTaskWaitForDocuments");
    verify(documentRequest, never()).hasCompleted("ReceiveTaskWaitForDocuments");

  }

  @Test
  public void testParsingAndDeployment() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }

}
