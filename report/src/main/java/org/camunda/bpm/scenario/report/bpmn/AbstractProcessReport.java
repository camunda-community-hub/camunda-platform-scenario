package org.camunda.bpm.scenario.report.bpmn;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.scenario.report.Report;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Martin Schimak
 */
abstract class AbstractProcessReport<T> implements Report<T> {

  private static final Map<String, List<HistoricActivityInstance>> coverageActivityInstances = new HashMap<>();
  private static final Map<String, BpmnModelInstance> coverageBpmnModelInstances = new HashMap<>();
  private static final Map<String, BpmnModelInstance> scenarioBpmnModelInstances = new HashMap<>();

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

  protected BpmnModelInstance getBpmnModelInstanceForCoverage(String processDefinitionKey) {
    return coverageBpmnModelInstances.get(processDefinitionKey).clone();
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

  protected List<HistoricActivityInstance> findActivityInstancesByProcessDefinitionKey(String processDefinitionKey) {
    return coverageActivityInstances.get(processDefinitionKey);
  }

}
