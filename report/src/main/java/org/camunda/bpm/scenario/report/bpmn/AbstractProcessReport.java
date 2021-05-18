package org.camunda.bpm.scenario.report.bpmn;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.scenario.report.Report;

import java.util.Map;

/**
 * @author Martin Schimak
 */
abstract class AbstractProcessReport<T> implements Report<T> {

  protected ProcessEngine processEngine;

  protected AbstractProcessReport(ProcessEngine processEngine) {
    this.processEngine = processEngine;
  }

  protected AbstractProcessReport() {
    this(processEngine());
  }

  private static ProcessEngine processEngine() {
    Map<String, ProcessEngine> processEngines = ProcessEngines.getProcessEngines();
    if (processEngines.size() == 1) {
      return processEngines.values().iterator().next();
    } else {
      String message = String.format("%s process engines registered with "
        + ProcessEngines.class.getSimpleName() + " class! ", processEngines.size()) +
        "As an alternative, you can explicitly initialize the engine by calling " +
        "the constructor with the engine as parameter.";
      throw new IllegalStateException(message);
    }
  }

}
