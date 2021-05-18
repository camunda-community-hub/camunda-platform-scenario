package org.camunda.bpm.scenario.report.bpmn;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Martin Schimak
 */
public class ProcessCoverageReport extends AbstractProcessReport<BpmnModelInstance> {

  static final Map<String, List<HistoricActivityInstance>> coverageActivityInstances = new HashMap<>();
  static final Map<String, BpmnModelInstance> coverageBpmnModelInstances = new HashMap<>();

  @Override
  public BpmnModelInstance generate(String processDefinitionKey) {
    List<HistoricActivityInstance> activityInstances = findActivityInstancesByProcessDefinitionKey(processDefinitionKey);
    return BpmnModelInstanceColoring.color(getBpmnModelInstanceForCoverage(processDefinitionKey), activityInstances);
  }

  protected BpmnModelInstance getBpmnModelInstanceForCoverage(String processDefinitionKey) {
    return coverageBpmnModelInstances.get(processDefinitionKey).clone();
  }

  protected List<HistoricActivityInstance> findActivityInstancesByProcessDefinitionKey(String processDefinitionKey) {
    return coverageActivityInstances.get(processDefinitionKey);
  }

}
