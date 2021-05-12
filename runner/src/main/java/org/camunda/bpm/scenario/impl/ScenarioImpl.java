package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.impl.util.Log;
import org.camunda.bpm.scenario.impl.util.Log.Action;
import org.camunda.bpm.scenario.impl.util.Time;
import org.camunda.bpm.scenario.run.ProcessRunner.StartableRunner;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="martin.schimak@plexiti.com">Martin Schimak</a>
 */
public class ScenarioImpl extends Scenario {

  private boolean executed;

  ProcessEngine processEngine;
  List<AbstractRunner> runners = new ArrayList<AbstractRunner>();

  List<BpmnModelInstance> mockedCallActivities = new ArrayList<>();
  private String deploymentId;

  public ScenarioImpl(ProcessScenario scenario) {
    this.runners.add(new ProcessRunnerImpl(this, scenario));
  }

  protected Scenario execute() {
    try {
      init();
      Time.init();
      List<Executable> executables;
      do {
        executables = new ArrayList<Executable>();
        for (AbstractRunner runner : runners) {
          executables.addAll(runner.next());
        }
        executables = Executable.Helpers.first(executables);
        if (!executables.isEmpty())
          executables.get(0).execute();
      } while (!executables.isEmpty());
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
    for (AbstractRunner runner: runners) {
      if (runner instanceof ProcessRunnerImpl) {
        ProcessRunnerImpl processRunner = (ProcessRunnerImpl) runner;
        if (processRunner.scenario == scenario) {
          instances.add(processRunner.processInstance);
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
            "Explicitely initialise engine by calling " + ScenarioImpl.class.getSimpleName() +
            "(scenario, engine)");
        throw new IllegalStateException(message);
      }
    }
    if (!mockedCallActivities.isEmpty()) {
      DeploymentBuilder deployment = processEngine.getRepositoryService().createDeployment();
      for (BpmnModelInstance mockedCallActivity: mockedCallActivities) {
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

  protected void cleanup() {
    if (deploymentId != null) {
      processEngine.getRepositoryService().deleteDeployment(deploymentId, true);
    }
  }

  public StartableRunner toBeStartedBy() {
    return (StartableRunner) runners.get(runners.size() - 1);
  }

}
