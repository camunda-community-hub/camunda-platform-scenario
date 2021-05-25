package org.camunda.bpm.scenario.report.bpmn;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.camunda.bpm.scenario.report.bpmn.ProcessCoverageReport.coverageActivityInstances;
import static org.camunda.bpm.scenario.report.bpmn.ProcessCoverageReport.coverageBpmnModelInstances;

/**
 * @author Martin Schimak
 */
public class ProcessScenarioReport extends AbstractProcessReport<BpmnModelInstance> {

  private final Map<String, BpmnModelInstance> scenarioBpmnModelInstances = new HashMap<>();

  @Override
  public BpmnModelInstance generate(String processInstanceId) {
    List<HistoricActivityInstance> activityInstances = findActivityInstancesByProcessInstanceId(processInstanceId);
    String processDefinitionId = activityInstances.get(0).getProcessDefinitionId();
    return BpmnModelInstanceColoring.color(getBpmnModelInstanceForScenario(processDefinitionId), activityInstances);
  }

  protected BpmnModelInstance getBpmnModelInstanceForScenario(String processDefinitionId) {
    String processDefinitionKey = processEngine.getRepositoryService().createProcessDefinitionQuery()
      .processDefinitionId(processDefinitionId).singleResult().getKey();
    BpmnModelInstance bpmnModelInstance = scenarioBpmnModelInstances.get(processDefinitionKey);
    if (bpmnModelInstance == null)
      bpmnModelInstance = processEngine.getRepositoryService().getBpmnModelInstance(processDefinitionId);
    for (Process process : bpmnModelInstance.getDefinitions().getChildElementsByType(Process.class)) {
      processDefinitionKey = process.getId();
      if (process.isExecutable()) {
        scenarioBpmnModelInstances.putIfAbsent(processDefinitionKey, bpmnModelInstance);
        coverageBpmnModelInstances.putIfAbsent(processDefinitionKey, bpmnModelInstance);
      }
    }
    return bpmnModelInstance;
  }

  protected List<HistoricActivityInstance> findActivityInstancesByProcessInstanceId(String processInstanceId) {
    List<HistoricActivityInstance> activityInstanceList = processEngine.getHistoryService().createHistoricActivityInstanceQuery()
      .processInstanceId(processInstanceId).list();
    if (!activityInstanceList.isEmpty()) {
      String processDefinitionKey = activityInstanceList.iterator().next().getProcessDefinitionKey();
      if (coverageActivityInstances.putIfAbsent(processDefinitionKey, activityInstanceList) != null) {
        coverageActivityInstances.get(processDefinitionKey).addAll(activityInstanceList);
      }
    }
    return activityInstanceList;
  }

}
