package org.camunda.bpm.scenario.report;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.scenario.report.bpmn.ProcessCoverageReport;
import org.camunda.bpm.scenario.report.bpmn.ProcessScenarioReport;
import org.camunda.bpm.scenario.report.bpmn.ProcessScenarioTestReportGenerator;

/**
 * @author Martin Schimak
 */
public interface Report<R> {

  R generate(String id);

  static Report<BpmnModelInstance> processScenarioReport() {
    return new ProcessScenarioReport();
  }

  static Report<BpmnModelInstance> processCoverageReport() {
    return new ProcessCoverageReport();
  }

}
