package org.camunda.bpm.scenarios;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.impl.persistence.entity.MessageEntity;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstantiationBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ScenarioRunner {

  private ScenarioStarter scenarioStarter;
  private String processDefinitionKey;

  private Map<String, Boolean> fromActivityIds = new HashMap<String, Boolean>();
  private Map<String, Boolean> toActivityIds = new HashMap<String, Boolean>();
  private Map<String, Object> startVariables = new HashMap<String, Object>();

  private Scenario scenario;
  private ProcessEngine processEngine;
  private ProcessInstance processInstance;

  public ScenarioRunner startBy(ScenarioStarter scenarioStarter) {
    this.scenarioStarter = scenarioStarter;
    return this;
  }

  public ScenarioRunner startBy(String processDefinitionKey) {
    this.processDefinitionKey = processDefinitionKey;
    return this;
  }

  public ScenarioRunner variables(Map<String, Object> variables) {
    this.startVariables = variables;
    return this;
  }

  public ScenarioRunner fromBefore(String activityId, String... activityIds) {
    put(true, true, activityId, activityIds);
    return this;
  }

  public ScenarioRunner fromAfter(String activityId, String... activityIds) {
    put(true, false, activityId, activityIds);
    return this;
  }

  public ScenarioRunner toBefore(String activityId, String... activityIds) {
    put(false, true, activityId, activityIds);
    return this;
  }

  public ScenarioRunner toAfter(String activityId, String... activityIds) {
    put(false, false, activityId, activityIds);
    return this;
  }

  private void put(Boolean from, Boolean before, String activityId, String... activityIds) {
    Map<String, Boolean> map = from ? fromActivityIds : toActivityIds;
    map.put(activityId, before);
    for (String a: activityIds) {
      map.put(a, before);
    }
  }

  private void init(ProcessEngine processEngine) {
    if (this.processEngine == null || processEngine != null) {
      if (processEngine == null) {
        Map<String, ProcessEngine> processEngines = ProcessEngines.getProcessEngines();
        if (processEngines.size() == 1) {
          this.processEngine = processEngines.values().iterator().next();
        }
        String message = processEngines.size() == 0 ? "No ProcessEngine found to be " +
            "registered with " + ProcessEngines.class.getSimpleName() + "!"
            : String.format(processEngines.size() + " ProcessEngines initialized. " +
            "Explicitely initialise engine by calling " + ScenarioRunner.class.getSimpleName() +
            "(scenario, engine)");
        throw new IllegalStateException(message);
      } else {
        this.processEngine = processEngine;
      }
    }
    if (this.scenarioStarter == null) {
      this.scenarioStarter = new ScenarioStarter() {
        @Override
        public ProcessInstance start() {
          ProcessInstantiationBuilder builder = ScenarioRunner.this.processEngine.getRuntimeService().createProcessInstanceByKey(processDefinitionKey);
          for (String activityId: fromActivityIds.keySet()) {
            Boolean from = fromActivityIds.get(activityId);
            if (from) {
              builder.startBeforeActivity(activityId);
            } else {
              builder.startAfterActivity(activityId);
            }
          }
          if (startVariables != null) {
            builder.setVariables(startVariables);
          }
          return builder.execute();
        }
      };
    }
  }

  public ProcessInstance run(Scenario scenario) {
    return run(scenario, null);
  }

  public ProcessInstance run(Scenario scenario, ProcessEngine processEngine) {
    init(processEngine);
    processInstance = scenarioStarter.start();
    do {
      continueAsynchronously();
    } while(true);
  }

  private AsyncContinuation nextAsyncContinuation() {
    List<Job> jobs = processEngine.getManagementService().createJobQuery().list();
    for (Job job: jobs) {
      if (job instanceof MessageEntity) {
        MessageEntity entity = (MessageEntity) job;
        if ("async-continuation".equals(entity.getJobHandlerType()))
          return new AsyncContinuation(processEngine, job.getExecutionId());
      }
    }
    return null;
  }

  private void continueAsynchronously() {
    AsyncContinuation asyncContinuation = nextAsyncContinuation();
    while (asyncContinuation != null) {
      asyncContinuation.leave();
      asyncContinuation = nextAsyncContinuation();
    }
  }

}
