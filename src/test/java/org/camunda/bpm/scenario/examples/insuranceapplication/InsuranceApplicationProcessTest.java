package org.camunda.bpm.scenario.examples.insuranceapplication;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.scenario.Scenario;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
@Deployment(resources = {"InsuranceApplication.bpmn", "DocumentRequest.bpmn", "RiskCheck.dmn"})
public class InsuranceApplicationProcessTest {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule();

  static {
    // LogUtil.readJavaUtilLoggingConfigFromClasspath(); // process engine
    // LogFactory.useJdkLogging(); // MyBatis
  }

  private Scenario insuranceApplicationScenario;
  private Scenario documentRequestScenario;

  private ProcessInstance insuranceApplication;
  private ProcessInstance documentRequest;

  private Map<String, Object> startVariables;

  @Before
  public void setupDefaultScenario() {

    insuranceApplicationScenario = mock(Scenario.class);
    documentRequestScenario = mock(Scenario.class);

    startVariables = Variables.createVariables()
      .putValue("applicantAge", "30")
      .putValue("carManufacturer", "VW")
      .putValue("carType", "Golf");

    when(insuranceApplicationScenario.atUserTask("UserTaskDecideAboutApplication")).thenReturn((task) -> {
      task.complete(withVariables("approved", true));
    });

    when(insuranceApplicationScenario.atUserTask("UserTaskCheckApplicationUnderwriter")).thenReturn((task) -> {
      assertThat(task).hasCandidateGroup("underwriter");
      task.complete();
    });

    when(insuranceApplicationScenario.atUserTask("UserTaskCheckApplicationTeamlead")).thenReturn((task) -> {
      assertThat(task).hasCandidateGroup("teamlead");
      task.complete();
    });

    when(insuranceApplicationScenario.atUserTask("UserTaskSpeedUpManualCheck")).thenReturn((task) -> {
      assertThat(task).hasCandidateGroup("management");
      task.complete();
    });

    when(insuranceApplicationScenario.atSendTask("SendTaskSendPolicy")).thenReturn((externalTask) -> {
      assertThat(externalTask.getTopicName()).isEqualTo("SendMail");
      externalTask.complete();
    });

    when(insuranceApplicationScenario.atSendTask("SendTaskSendRejection")).thenReturn((externalTask) -> {
      assertThat(externalTask.getTopicName()).isEqualTo("SendMail");
      externalTask.complete();
    });

    when(insuranceApplicationScenario.atCallActivity("CallActivityDocumentRequest")).thenReturn((processInstance) -> {
      assertThat(processInstance).isStarted();
      documentRequest = processInstance.runner().start(documentRequestScenario);
    });

    when(documentRequestScenario.atSendTask("SendTaskRequestDocuments")).thenReturn((externalTask) -> {
      assertThat(externalTask.getTopicName()).isEqualTo("SendMail");
      externalTask.complete();
    });

    when(documentRequestScenario.atReceiveTask("ReceiveTaskWaitForDocuments")).thenReturn((receiveTask) -> {
      assertThat(receiveTask.getEventType()).isEqualTo("message");
      assertThat(receiveTask.getEventName()).isEqualTo("MSG_DOCUMENT_RECEIVED");
      receiveTask.receiveMessage();
    });

    when(documentRequestScenario.atUserTask("UserTaskCallCustomer")).thenReturn((task) -> {
      task.complete();
    });

    when(documentRequestScenario.atSendTask("SendTaskSendReminder")).thenReturn((externalTask) -> {
      assertThat(externalTask.getTopicName()).isEqualTo("SendMail");
      externalTask.complete();
    });

  }

  @Test
  public void testParsingAndDeployment() {
    // nothing is done here, as we just want to check for exceptions during deployment
  }

  @Test
  public void testGreenScenario() {

    // when

    insuranceApplication = Scenario.run("InsuranceApplication")
        .variables(startVariables)
        .start(insuranceApplicationScenario);

    // then

    assertThat(insuranceApplication)
        .isEnded()
        .hasPassed("EndEventApplicationAccepted")
        .hasNotPassed("SubProcessManualCheck")
        .variables().containsEntry("riskAssessment", "green");

    // and

    verify(insuranceApplicationScenario, times(1)).hasCompleted("EndEventApplicationAccepted");
    verify(insuranceApplicationScenario, never()).hasFinished("SubProcessManualCheck");

  }

  @Test
  public void testYellowScenario() {

    // given
    startVariables = Variables.createVariables()
        .putValue("applicantAge", 30)
        .putValue("carManufacturer", "Porsche")
        .putValue("carType", "911");

    // when

    insuranceApplication = Scenario.run("InsuranceApplication")
        .variables(startVariables)
        .start(insuranceApplicationScenario);

    // then

    assertThat(insuranceApplication)
        .isEnded()
        .hasPassed("SubProcessManualCheck", "EndEventApplicationAccepted")
        .variables().containsEntry("riskAssessment", "yellow");

  }

  @Test
  public void testRedScenario() {

    // given

    startVariables = Variables.createVariables()
        .putValue("applicantAge", 20)
        .putValue("carManufacturer", "Porsche")
        .putValue("carType", "911");

    // when

    insuranceApplication = Scenario.run("InsuranceApplication")
        .variables(startVariables)
        .start(insuranceApplicationScenario);

    // then

    assertThat(insuranceApplication)
        .isEnded()
        .hasNotPassed("SubProcessManualCheck")
        .hasPassed("EndEventApplicationRejected")
        .variables().containsEntry("riskAssessment", "red");

  }

  @Test
  public void testManualApprovalScenario() {

    // given

    startVariables = Variables.createVariables()
        .putValue("applicantAge", 30)
        .putValue("carManufacturer", "Porsche")
        .putValue("carType", "911");

    // when

    insuranceApplication = Scenario.run("InsuranceApplication")
        .variables(startVariables)
        .start(insuranceApplicationScenario);

    // then

    assertThat(insuranceApplication)
        .isEnded()
        .hasPassed("SubProcessManualCheck", "EndEventApplicationAccepted")
        .variables()
        .containsEntry("riskAssessment", "yellow")
        .containsEntry("approved", true);

  }

  @Test
  public void testManualRejectionScenario() {

    // given

    startVariables = Variables.createVariables()
        .putValue("applicantAge", 30)
        .putValue("carManufacturer", "Porsche")
        .putValue("carType", "911");

    // and

    when(insuranceApplicationScenario.atUserTask("UserTaskDecideAboutApplication")).thenReturn((task) -> {
      task.complete(withVariables("approved", false));
    });

    // when

    insuranceApplication = Scenario.run("InsuranceApplication")
        .variables(startVariables)
        .start(insuranceApplicationScenario);

    // then

    assertThat(insuranceApplication)
        .isEnded()
        .hasPassed("SubProcessManualCheck", "EndEventApplicationRejected")
        .variables()
        .containsEntry("riskAssessment", "yellow")
        .containsEntry("approved", false);

  }

  @Test
  public void testDocumentRequestScenario() {

    // given

    startVariables = Variables.createVariables()
        .putValue("applicantAge", 30)
        .putValue("carManufacturer", "Porsche")
        .putValue("carType", "911");

    // and

    when(insuranceApplicationScenario.atUserTask("UserTaskDecideAboutApplication")).thenReturn((task) -> {
      runtimeService().correlateMessage("msgDocumentNecessary");
      task.complete(withVariables("approved", true));
    });

    // when

    insuranceApplication = Scenario.run("InsuranceApplication")
        .variables(startVariables)
        .start(insuranceApplicationScenario);

    // then

    assertThat(insuranceApplication)
        .isEnded()
        .hasPassed("CallActivityDocumentRequest");
  }

  @Test
  public void testDocumentRequestBitLateScenario() {

    // given

    startVariables = Variables.createVariables()
        .putValue("applicantAge", 30)
        .putValue("carManufacturer", "Porsche")
        .putValue("carType", "911");

    // and

    when(insuranceApplicationScenario.atUserTask("UserTaskDecideAboutApplication")).thenReturn((task) -> {
      runtimeService().correlateMessage("msgDocumentNecessary");
      task.complete(withVariables("approved", true));
    });

    when(documentRequestScenario.atReceiveTask("ReceiveTaskWaitForDocuments")).thenReturn((receiveTask) -> {
      receiveTask.triggerTimer("BoundaryEventDaily");
      receiveTask.receiveMessage();
    });

    // when

    insuranceApplication = Scenario.run("InsuranceApplication")
        .variables(startVariables)
        .start(insuranceApplicationScenario);

    // then

    assertThat(insuranceApplication)
      .isEnded()
      .hasPassed("CallActivityDocumentRequest");

    // and

    assertThat(documentRequest)
      .hasPassed("SendTaskSendReminder");

  }

  @Test
  public void testDocumentRequestVeryLateScenario() {

    // given

    startVariables = Variables.createVariables()
        .putValue("applicantAge", 30)
        .putValue("carManufacturer", "Porsche")
        .putValue("carType", "911");

    // and

    when(insuranceApplicationScenario.atUserTask("UserTaskDecideAboutApplication")).thenReturn((task) -> {
      runtimeService().correlateMessage("msgDocumentNecessary");
      task.complete(withVariables("approved", true));
    });

    when(documentRequestScenario.atReceiveTask("ReceiveTaskWaitForDocuments")).thenReturn((receiveTask) -> {
      receiveTask.triggerTimer("BoundaryEventDaily");
      receiveTask.triggerTimer("BoundaryEventDaily");
      receiveTask.triggerTimer("BoundaryEventDaily");
      receiveTask.triggerTimer("BoundaryEventOneWeek");
    });

    // when

    insuranceApplication = Scenario.run("InsuranceApplication")
        .variables(startVariables)
        .start(insuranceApplicationScenario);

    // then

    verify(documentRequestScenario, times(1)).hasCompleted("UserTaskCallCustomer");
    verify(documentRequestScenario, times(3)).hasCompleted("SendTaskSendReminder");
    verify(documentRequestScenario).hasCanceled("ReceiveTaskWaitForDocuments");
    verify(documentRequestScenario, never()).hasCompleted("ReceiveTaskWaitForDocuments");

    // and
    verify(insuranceApplicationScenario).hasCompleted("EndEventApplicationAccepted");
    verify(insuranceApplicationScenario, times(15)).hasFinished(any());

  }

}
