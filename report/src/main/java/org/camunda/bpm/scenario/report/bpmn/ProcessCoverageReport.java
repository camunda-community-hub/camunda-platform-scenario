package org.camunda.bpm.scenario.report.bpmn;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import java.util.List;

/**
 * @author Martin Schimak
 */
public class ProcessCoverageReport extends AbstractProcessReport<BpmnModelInstance> {

  @Override
  public BpmnModelInstance generate(String processDefinitionKey) {
    List<HistoricActivityInstance> activityInstances = findActivityInstancesByProcessDefinitionKey(processDefinitionKey);
    return BpmnModelInstanceColoring.color(getBpmnModelInstanceByProcessDefinitionKey(processDefinitionKey), activityInstances);
  }

}
