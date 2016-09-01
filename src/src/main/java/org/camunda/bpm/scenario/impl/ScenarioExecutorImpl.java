package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.ExecutedScenario;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.impl.util.Time;
import org.camunda.bpm.scenario.run.ProcessRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ScenarioExecutorImpl implements ExecutedScenario {

  private boolean executed;

  ProcessEngine processEngine;
  List<Runner> runners = new ArrayList<Runner>();

  public ScenarioExecutorImpl(ProcessScenario scenario) {
    this.runners.add(new ProcessRunnerImpl(this, scenario));
  }

  protected ExecutedScenario execute() {
    init();
    Time.init();
    List<Executable> executables;
    do {
      executables = new ArrayList<Executable>();
      for (Runner runner: runners) {
        executables.addAll(runner.next());
      }
      executables = Executable.Helpers.first(executables);
      if (!executables.isEmpty())
        executables.get(0).execute();
    } while (!executables.isEmpty());
    Time.reset();
    return this;
  }

  @Override
  public ProcessInstance getInstance(ProcessScenario scenario) {
    List<ProcessInstance> instances = getInstances(scenario);
    if (instances.size() > 1)
      throw new IllegalStateException("Scenario executed more than a single process instance based on the scenario provided as a parameter");
    return instances.size() == 1 ? instances.get(0) : null;
  }

  @Override
  public List<ProcessInstance> getInstances(ProcessScenario scenario) {
    List<ProcessInstance> instances = new ArrayList<ProcessInstance>();
    for (Runner runner: runners) {
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
            "Explicitely initialise engine by calling " + ScenarioExecutorImpl.class.getSimpleName() +
            "(scenario, engine)");
        throw new IllegalStateException(message);
      }
    }
  }

  protected void init(ProcessEngine processEngine) {
    this.processEngine = processEngine;
  }

  public ProcessRunner.ToBeStartedBy toBeStartedBy() {
    return (ProcessRunner.ToBeStartedBy) runners.get(runners.size() - 1);
  }

}
