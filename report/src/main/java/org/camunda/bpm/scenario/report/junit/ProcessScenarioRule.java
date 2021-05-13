package org.camunda.bpm.scenario.report.junit;

import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.scenario.report.bpmn.ProcessScenarioTestReportGenerator;
import org.junit.runner.Description;

/**
 * @author Martin Schimak
 */
public class ProcessScenarioRule extends ProcessEngineRule {

  @Override
  protected void succeeded(Description description) {
    generateProcessScenarioTestReport(description);
    super.succeeded(description);
  }

  @Override
  protected void failed(Throwable throwable, Description description) {
    generateProcessScenarioTestReport(description);
    super.failed(throwable, description);
  }

  private void generateProcessScenarioTestReport(Description description) {
    new ProcessScenarioTestReportGenerator(description.getClassName(), description.getMethodName()).generate(deploymentId);
  }

}
