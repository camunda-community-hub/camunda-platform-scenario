package org.camunda.bpm.scenario.report.bpmn;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import java.util.List;

/**
 * @author Martin Schimak
 */
public class ProcessScenarioReport extends AbstractProcessReport<BpmnModelInstance> {

  @Override
  public BpmnModelInstance generate(String processInstanceId) {
    List<HistoricActivityInstance> activityInstances = findActivityInstancesByProcessInstanceId(processInstanceId);
    String processDefinitionId = activityInstances.size() > 0 ? activityInstances.get(0).getProcessDefinitionId() : null;
    return BpmnModelInstanceColoring.color(getBpmnModelInstanceByProcessDefinitionId(processDefinitionId), activityInstances);
  }

}
