package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.impl.job.ExecutableTimerJob;
import org.camunda.bpm.scenario.runner.ScenarioRun;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ScenarioExecutorImpl {

  private boolean executed;

  public ProcessEngine processEngine;
  public List<Runner> runners = new ArrayList<Runner>();

  public ScenarioExecutorImpl(Scenario.Process scenario) {
    this.runners.add(new ProcessRunnerImpl(this, scenario));
  }

  protected ScenarioRun execute() {
    init();
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
    return new ScenarioRun() {
      @Override
      public ProcessInstance getProcessInstance() {
        return ((ProcessRunnerImpl) runners.get(0)).processInstance;
      }
    };
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
    ExecutableTimerJob.init();
  }

  protected void init(ProcessEngine processEngine) {
    this.processEngine = processEngine;
  }

}
