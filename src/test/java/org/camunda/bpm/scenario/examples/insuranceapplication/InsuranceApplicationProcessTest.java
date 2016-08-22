package org.camunda.bpm.scenario.examples.insuranceapplication;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.scenario.Scenario;
import org.junit.Before;
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
@Deployment(resources = {"InsuranceApplication.bpmn", "DocumentRequest.bpmn", "RiskCheck.dmn"})
public class InsuranceApplicationProcessTest {

  @Rule public ProcessEngineRule rule = new ProcessEngineRule();

  // Mock all waitstates in main process and call activity with a scenario
  @Mock private Scenario insuranceApplication;
  @Mock private Scenario documentRequest;
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

    when(insuranceApplication.atUserTask("UserTaskDecideAboutApplication")).thenReturn((task) -> {
      task.complete(withVariables("approved", true));
    });

    when(insuranceApplication.atUserTask("UserTaskCheckApplicationUnderwriter")).thenReturn((task) -> {
      assertThat(task).hasCandidateGroup("underwriter");
      task.complete();
    });

    when(insuranceApplication.atUserTask("UserTaskCheckApplicationTeamlead")).thenReturn((task) -> {
      assertThat(task).hasCandidateGroup("teamlead");
      task.complete();
    });

    when(insuranceApplication.atUserTask("UserTaskSpeedUpManualCheck")).thenReturn((task) -> {
      assertThat(task).hasCandidateGroup("management");
      task.complete();
    });

    when(insuranceApplication.atSendTask("SendTaskSendPolicy")).thenReturn((externalTask) -> {
      assertThat(externalTask.getTopicName()).isEqualTo("SendMail");
      externalTask.complete();
    });

    when(insuranceApplication.atSendTask("SendTaskSendRejection")).thenReturn((externalTask) -> {
      assertThat(externalTask.getTopicName()).isEqualTo("SendMail");
      externalTask.complete();
    });

    when(insuranceApplication.atCallActivity("CallActivityDocumentRequest")).thenReturn((processInstance) -> {
      assertThat(processInstance).isStarted();
      processInstance.runner().start(documentRequest);
    });

    when(documentRequest.atSendTask("SendTaskRequestDocuments")).thenReturn((externalTask) -> {
      assertThat(externalTask.getTopicName()).isEqualTo("SendMail");
      externalTask.complete();
    });

    when(documentRequest.atReceiveTask("ReceiveTaskWaitForDocuments")).thenReturn((receiveTask) -> {
      assertThat(receiveTask.getEventType()).isEqualTo("message");
      assertThat(receiveTask.getEventName()).isEqualTo("MSG_DOCUMENT_RECEIVED");
      receiveTask.receiveMessage();
    });

    when(documentRequest.atUserTask("UserTaskCallCustomer")).thenReturn((task) -> {
      task.complete();
    });

    when(documentRequest.atSendTask("SendTaskSendReminder")).thenReturn((externalTask) -> {
      assertThat(externalTask.getTopicName()).isEqualTo("SendMail");
      externalTask.complete();
    });

  }

  @Test
  public void testGreenScenario() {

    // when

    ProcessInstance pi = Scenario.run("InsuranceApplication") // either just start process by key ...
      .variables(variables)
      .start(insuranceApplication);

    // then

    assertThat(pi).variables().containsEntry("riskAssessment", "green");
    verify(insuranceApplication, never()).hasStarted("SubProcessManualCheck");
    verify(insuranceApplication).hasFinished("EndEventApplicationAccepted");

  }

  @Test
  public void testYellowScenario() {

    // given
    variables = Variables.createVariables()
      .putValue("applicantAge", 30)
      .putValue("carManufacturer", "Porsche")
      .putValue("carType", "911");

    // when

    ProcessInstance pi = Scenario.run(() -> { // ... or define your own starter function
        return rule.getRuntimeService().startProcessInstanceByKey("InsuranceApplication", variables);
      })
      .start(insuranceApplication);

    // then

    assertThat(pi).variables().containsEntry("riskAssessment", "yellow");
    verify(insuranceApplication).hasCompleted("SubProcessManualCheck");
    verify(insuranceApplication).hasFinished("EndEventApplicationAccepted");

  }

  @Test
  public void testRedScenario() {

    // given

    variables = Variables.createVariables()
      .putValue("applicantAge", 20)
      .putValue("carManufacturer", "Porsche")
      .putValue("carType", "911");

    // when

    ProcessInstance pi = Scenario.run("InsuranceApplication")
      .variables(variables)
      .start(insuranceApplication);

    // then

    assertThat(pi).variables().containsEntry("riskAssessment", "red");

    verify(insuranceApplication, never()).hasStarted("SubProcessManualCheck");
    verify(insuranceApplication).hasFinished("EndEventApplicationRejected");

  }

  @Test
  public void testManualApprovalScenario() {

    // given

    variables = Variables.createVariables()
      .putValue("applicantAge", 30)
      .putValue("carManufacturer", "Porsche")
      .putValue("carType", "911");

    // when

    ProcessInstance pi = Scenario.run("InsuranceApplication")
      .variables(variables)
      .start(insuranceApplication);

    // then

    assertThat(pi).variables()
      .containsEntry("riskAssessment", "yellow")
      .containsEntry("approved", true);

    verify(insuranceApplication).hasCompleted("SubProcessManualCheck");
    verify(insuranceApplication).hasFinished("EndEventApplicationAccepted");

  }

  @Test
  public void testManualRejectionScenario() {

    // given

    variables = Variables.createVariables()
      .putValue("applicantAge", 30)
      .putValue("carManufacturer", "Porsche")
      .putValue("carType", "911");

    when(insuranceApplication.atUserTask("UserTaskDecideAboutApplication")).thenReturn((task) -> {
      task.complete(withVariables("approved", false));
    });

    // when

    ProcessInstance pi = Scenario.run("InsuranceApplication")
      .variables(variables)
      .start(insuranceApplication);

    // then

    assertThat(pi).variables()
      .containsEntry("riskAssessment", "yellow")
      .containsEntry("approved", false);

    verify(insuranceApplication).hasCompleted("SubProcessManualCheck");
    verify(insuranceApplication).hasFinished("EndEventApplicationRejected");

  }

  @Test
  public void testDocumentRequestScenario() {

    // given

    variables = Variables.createVariables()
        .putValue("applicantAge", 30)
        .putValue("carManufacturer", "Porsche")
        .putValue("carType", "911");

    when(insuranceApplication.atUserTask("UserTaskDecideAboutApplication")).thenReturn((task) -> {
      runtimeService().correlateMessage("msgDocumentNecessary");
      task.complete(withVariables("approved", true));
    });

    // when

    Scenario.run("InsuranceApplication")
      .variables(variables)
      .start(insuranceApplication);

    // then

    verify(insuranceApplication).hasCompleted("CallActivityDocumentRequest");

  }

  @Test
  public void testDocumentRequestBitLateScenario() {

    // given

    variables = Variables.createVariables()
      .putValue("applicantAge", 30)
      .putValue("carManufacturer", "Porsche")
      .putValue("carType", "911");

    when(insuranceApplication.atUserTask("UserTaskDecideAboutApplication")).thenReturn((task) -> {
      runtimeService().correlateMessage("msgDocumentNecessary");
      task.complete(withVariables("approved", true));
    });

    when(documentRequest.atReceiveTask("ReceiveTaskWaitForDocuments")).thenReturn((receiveTask) -> {
      receiveTask.triggerTimer("BoundaryEventDaily");
      receiveTask.receiveMessage();
    });

    // when

    Scenario.run("InsuranceApplication")
      .variables(variables)
      .start(insuranceApplication);

    // then

    verify(insuranceApplication).hasCompleted("CallActivityDocumentRequest");
    verify(documentRequest).hasCompleted("SendTaskSendReminder");

  }

  @Test
  public void testDocumentRequestVeryLateScenario() {

    // given

    variables = Variables.createVariables()
        .putValue("applicantAge", 30)
        .putValue("carManufacturer", "Porsche")
        .putValue("carType", "911");

    when(insuranceApplication.atUserTask("UserTaskDecideAboutApplication")).thenReturn((task) -> {
      runtimeService().correlateMessage("msgDocumentNecessary");
      task.complete(withVariables("approved", true));
    });

    when(documentRequest.atReceiveTask("ReceiveTaskWaitForDocuments")).thenReturn((receiveTask) -> {
      receiveTask.triggerTimer("BoundaryEventDaily");
      receiveTask.triggerTimer("BoundaryEventDaily");
      receiveTask.triggerTimer("BoundaryEventDaily");
      receiveTask.triggerTimer("BoundaryEventOneWeek");
    });

    // when

    Scenario.run("InsuranceApplication")
      .variables(variables)
      .start(insuranceApplication);

    // then

    verify(insuranceApplication).hasCompleted("EndEventApplicationAccepted");

    verify(documentRequest, times(1)).hasCompleted("UserTaskCallCustomer");
    verify(documentRequest, times(3)).hasCompleted("SendTaskSendReminder");
    verify(documentRequest).hasCanceled("ReceiveTaskWaitForDocuments");
    verify(documentRequest, never()).hasCompleted("ReceiveTaskWaitForDocuments");

  }

  @Test
  public void testParsingAndDeployment() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }

}
