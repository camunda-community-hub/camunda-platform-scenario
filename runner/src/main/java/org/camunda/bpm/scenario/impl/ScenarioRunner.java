package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.ActivityTypes;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.impl.delegate.HistoricProcessInstanceDelegateImpl;
import org.camunda.bpm.scenario.impl.util.Time;
import org.camunda.bpm.scenario.run.ProcessRunner.StartableRunner;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Martin Schimak
 */
public class ScenarioRunner extends Scenario {

  ProcessEngine processEngine;
  List<ProcessInstanceRunner> runners = new ArrayList<>();
  List<BpmnModelInstance> mockedProcesses = new ArrayList<>();

  private boolean executed;
  private String deploymentId;
  private List<String> preExistingProcessInstanceIds;

  public ScenarioRunner(ProcessScenario scenario) {
    runners.add(new ProcessInstanceRunner(this, scenario));
  }

  protected Scenario execute() {
    try {
      init();
      Time.init();
      List<Executable> executables;
      boolean caughtSomething;
      do {
        executables = new ArrayList<>();
        for (ProcessInstanceRunner runner : runners) {
          executables.addAll(runner.next());
        }
        executables = Executable.Helpers.first(executables);
        if (!executables.isEmpty())
          executables.get(0).execute();
        caughtSomething = catchAndRun(); // TODO when to call this so that the log appears in the correct order?
      } while (!executables.isEmpty() || caughtSomething);
    } finally {
      Time.reset();
      cleanup();
    }
    return this;
  }

  @Override
  public ProcessInstance instance(ProcessScenario scenario) {
    List<ProcessInstance> instances = instances(scenario);
    if (instances.size() > 1)
      throw new IllegalStateException("Scenario executed more than a single process instance based on the scenario provided as a parameter");
    return instances.size() == 1 ? instances.get(0) : null;
  }

  @Override
  public List<ProcessInstance> instances(ProcessScenario scenario) {
    List<ProcessInstance> instances = new ArrayList<ProcessInstance>();
    for (AbstractRunner runner : runners) {
      if (runner instanceof ProcessInstanceRunner) {
        ProcessInstanceRunner processInstanceRunner = (ProcessInstanceRunner) runner;
        if (processInstanceRunner.processScenario == scenario) {
          instances.add(processInstanceRunner.processInstance);
        }
      }
    }
    return instances;
  }

  protected void init() {
    if (executed)
      throw new IllegalStateException("Scenarios may use execute() just once per Scenario.run(). " +
        "Please create a new Scenario.run().");
    executed = true;
    if (processEngine == null) {
      Map<String, ProcessEngine> processEngines = ProcessEngines.getProcessEngines();
      if (processEngines.size() == 1) {
        init(processEngines.values().iterator().next());
      } else {
        String message = processEngines.size() == 0 ? "No ProcessEngine found to be " +
          "registered with " + ProcessEngines.class.getSimpleName() + "!"
          : String.format(processEngines.size() + " ProcessEngines initialized. " +
          "Explicitely initialise engine by calling " + ScenarioRunner.class.getSimpleName() +
          "(scenario, engine)");
        throw new IllegalStateException(message);
      }
    }
    preExistingProcessInstanceIds = processEngine.getHistoryService().
      createHistoricProcessInstanceQuery().list().stream()
      .map(processInstance -> processInstance.getId())
      .collect(Collectors.toList());
    if (!mockedProcesses.isEmpty()) {
      DeploymentBuilder deployment = processEngine.getRepositoryService().createDeployment();
      for (BpmnModelInstance mockedCallActivity : mockedProcesses) {
        String processDefinitionKey = mockedCallActivity
          .getDefinitions().getChildElementsByType(Process.class).iterator().next().getId();
        boolean exists = !processEngine.getRepositoryService().createProcessDefinitionQuery()
          .processDefinitionKey(processDefinitionKey).list().isEmpty();
        if (exists)
          throw new AssertionError("Process '" + processDefinitionKey + "' declared to be mocked, " +
            "but it is already deployed. Please remove from your list of explicit deployments.");
        deployment.addModelInstance(processDefinitionKey + ".bpmn", mockedCallActivity);
      }
      deploymentId = deployment.deploy().getId();
    }
  }

  protected void init(ProcessEngine processEngine) {
    this.processEngine = processEngine;
  }

  protected boolean catchAndRun() {
    List<String> managedProcessInstanceIds = runners.stream().map(r -> r.processInstance.getId()).collect(Collectors.toList());
    List<HistoricProcessInstance> processInstances = processEngine.getHistoryService().createHistoricProcessInstanceQuery().list();
    List<HistoricProcessInstance> startedInstances = processInstances.stream()
      .filter(processInstance -> !managedProcessInstanceIds.contains(processInstance.getId())
        && !preExistingProcessInstanceIds.contains(processInstance.getId()))
      .collect(Collectors.toList());
    for (HistoricProcessInstance startedInstance : startedInstances) {
      ProcessInstanceRunner childRunner = null;
      Iterator<ProcessInstanceRunner> processInstanceRunners = runners.iterator();
      while (processInstanceRunners.hasNext()) {
        ProcessInstanceRunner instanceRunner = processInstanceRunners.next();
        if (startedInstance.getSuperProcessInstanceId() == null) {
          childRunner = (ProcessInstanceRunner) instanceRunner.processScenario.
            runsProcessInstance(startedInstance.getProcessDefinitionKey());
          if (childRunner == null && !processInstanceRunners.hasNext()) {
            throw new AssertionError("Unexpected Process Instance {"
              + startedInstance.getProcessDefinitionId() + ", " + startedInstance.getId() + "} "
              + "started during scenario run, but not process scenario has been defined for it.");
          }
        } else if(instanceRunner.processInstance.getId().equals(startedInstance.getSuperProcessInstanceId())) {
          Optional<HistoricActivityInstance> callActivity = processEngine.getHistoryService().createHistoricActivityInstanceQuery()
            .processInstanceId(startedInstance.getSuperProcessInstanceId())
            .activityType(ActivityTypes.CALL_ACTIVITY).finished().list()
            .stream().filter(activityInstance -> activityInstance.getCalledProcessInstanceId().equals(startedInstance.getId()))
            .findFirst();
          if (callActivity.isPresent() ) {
            childRunner = (ProcessInstanceRunner) instanceRunner.processScenario.runsCallActivity(callActivity.get().getActivityId());
            if (childRunner == null && !processInstanceRunners.hasNext()) {
              throw new AssertionError("Unexpected Process Instance {"
                + startedInstance.getProcessDefinitionId() + ", " + startedInstance.getId() + "} "
                + "started during scenario run, but not process scenario has been defined for it.");
            }
          }
        }
        if (childRunner != null) {
          runners.add(childRunner);
          childRunner.scenarioRunner = this;
          childRunner.processInstance = processEngine.getRuntimeService()
            .createProcessInstanceQuery().processInstanceId(startedInstance.getId()).singleResult();
          if (childRunner.processInstance == null)
            childRunner.processInstance = new HistoricProcessInstanceDelegateImpl(startedInstance);
          childRunner.processDefinitionKey = processEngine.getRepositoryService()
            .createProcessDefinitionQuery()
            .processDefinitionId(startedInstance.getProcessDefinitionId())
            .singleResult().getKey();
          childRunner.businessKey = startedInstance.getBusinessKey();
          childRunner.setExecuted();
          return true;
        }
      }
    }
    return false;
  }

  protected void cleanup() {
    if (deploymentId != null) {
      processEngine.getRepositoryService().deleteDeployment(deploymentId, true);
      deploymentId = null;
    }
  }

  public StartableRunner toBeStartedBy() {
    return (StartableRunner) runners.get(runners.size() - 1);
  }

}
