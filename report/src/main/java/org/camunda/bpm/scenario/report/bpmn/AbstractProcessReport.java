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

  private static final Map<String, List<HistoricActivityInstance>> historicActivityInstances = new HashMap<>();
  private static final Map<String, BpmnModelInstance> bpmnModelInstances = new HashMap<>();

  protected ProcessEngine processEngine;

  public AbstractProcessReport(ProcessEngine processEngine) {
    this.processEngine = processEngine;
  }

  public AbstractProcessReport() {
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

  protected BpmnModelInstance getBpmnModelInstanceByProcessDefinitionId(String processDefinitionId) {
    BpmnModelInstance bpmnModelInstance = processEngine.getRepositoryService().getBpmnModelInstance(processDefinitionId);
    bpmnModelInstance.getDefinitions().getChildElementsByType(Process.class).forEach(process -> {
      String processDefinitionKey = process.getId();
      if (process.isExecutable())
        bpmnModelInstances.putIfAbsent(processDefinitionKey, bpmnModelInstance);
    });
    return bpmnModelInstance;
  }

  protected BpmnModelInstance getBpmnModelInstanceByProcessDefinitionKey(String processDefinitionKey) {
    return bpmnModelInstances.get(processDefinitionKey).clone();
  }

  protected List<HistoricActivityInstance> findActivityInstancesByProcessInstanceId(String processInstanceId) {
    List<HistoricActivityInstance> activityInstanceList = processEngine.getHistoryService().createHistoricActivityInstanceQuery()
      .processInstanceId(processInstanceId).list();
    if (!activityInstanceList.isEmpty()) {
      String processDefinitionKey = activityInstanceList.iterator().next().getProcessDefinitionKey();
      if (historicActivityInstances.putIfAbsent(processDefinitionKey, activityInstanceList) != null) {
        historicActivityInstances.get(processDefinitionKey).addAll(activityInstanceList);
      }
    }
    return activityInstanceList;
  }

  protected List<HistoricActivityInstance> findActivityInstancesByProcessDefinitionId(String processDefinitionId) {
    return processEngine.getHistoryService().createHistoricActivityInstanceQuery()
      .processDefinitionId(processDefinitionId).list();
  }

  protected List<HistoricActivityInstance> findActivityInstancesByProcessDefinitionKey(String processDefinitionKey) {
    return historicActivityInstances.get(processDefinitionKey);
  }

  protected String getLatestProcessDefinitionId(String processDefinitionKey) {
    return processEngine.getRepositoryService().createProcessDefinitionQuery()
      .processDefinitionKey(processDefinitionKey).latestVersion().singleResult().getId();
  }

}
