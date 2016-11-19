package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.impl.persistence.entity.JobEntity;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.scenario.defer.Deferred;
import org.camunda.bpm.scenario.impl.job.ContinuationExecutable;
import org.camunda.bpm.scenario.impl.waitstate.IgnoredExecutable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
@SuppressWarnings("unchecked")
public interface Executable<S> extends Comparable<S> {

  void execute();

  class Waitstates {

    static Map<String, String> types = new HashMap<String, String>(); static {
      types.put("userTask", "UserTaskExecutable");
      types.put("intermediateSignalCatch", "SignalIntermediateCatchEventExecutable");
      types.put("intermediateMessageCatch", "MessageIntermediateCatchEventExecutable");
      types.put("receiveTask", "ReceiveTaskExecutable");
      types.put("intermediateTimer", "TimerIntermediateEventExecutable");
      types.put("intermediateConditional", "ConditionalIntermediateEventExecutable");
      types.put("eventBasedGateway", "EventBasedGatewayExecutable");
      types.put("callActivity", "CallActivityExecutable");
      types.put("serviceTask", "ServiceTaskExecutable");
      types.put("businessRuleTask", "BusinessRuleTaskExecutable");
      types.put("sendTask", "SendTaskExecutable");
      types.put("intermediateMessageThrowEvent", "MessageIntermediateThrowEventExecutable");
      types.put("messageEndEvent", "MessageEndEventExecutable");
    }

    static WaitstateExecutable newInstance(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
      if (!runner.isExecuted(instance)) {
        String type = instance.getActivityType();
        if (types.containsKey(type)) {
          try {
            return (WaitstateExecutable) Class.forName(IgnoredExecutable.class.getPackage().getName() + "." + types.get(type)).getConstructor(ProcessRunnerImpl.class, HistoricActivityInstance.class).newInstance(runner, instance);
          } catch (Exception e) {
            throw new IllegalArgumentException(e);
          }
        }
        return new IgnoredExecutable(runner, instance);
      }
      return null;
    }

    static List<Executable> next(ProcessRunnerImpl runner) {
      List<HistoricActivityInstance> instances = runner.scenarioExecutor.processEngine
          .getHistoryService().createHistoricActivityInstanceQuery()
          .processInstanceId(runner.processInstance.getId()).unfinished().list();
      return Helpers.next(runner, instances);
    }

  }

  class Jobs {

    static Map<String, String> types = new HashMap<String, String>(); static {
      types.put("async-continuation", "ContinuationExecutable");
      types.put("timer-transition", "TimerJobExecutable");
      types.put("timer-intermediate-transition", "TimerJobExecutable");
      types.put("timer-start-event-subprocess", "TimerJobExecutable");
    }

    static JobExecutable newInstance(ProcessRunnerImpl runner, Job job) {
      JobEntity entity = (JobEntity) job;
      String type = entity.getJobHandlerType();
      if (types.containsKey(type)) {
        try {
          return (JobExecutable) Class.forName(ContinuationExecutable.class.getPackage().getName() + "." + types.get(type)).getConstructor(ProcessRunnerImpl.class, Job.class).newInstance(runner, job);
        } catch (Exception e) {
          throw new IllegalArgumentException(e);
        }
      }
      return null;
    }

    static List<Executable> next(ProcessRunnerImpl runner) {
      List<Job> jobs = runner.scenarioExecutor.processEngine.getManagementService()
          .createJobQuery().processInstanceId(runner.processInstance.getId()).list();
      return Helpers.next(runner, jobs);
    }

  }

  class Deferreds {

    private static Map<String, List<DeferredExecutable>> executablesMap = new HashMap<String, List<DeferredExecutable>>();

    public static DeferredExecutable newInstance(ProcessRunnerImpl runner, HistoricActivityInstance instance, String period, Deferred action) {
      return new DeferredExecutable(runner, instance, period, action);
    }

    static List<Executable> next(ProcessRunnerImpl runner) {
      List<Executable> e = new ArrayList<Executable>();
      Collection<List<DeferredExecutable>> executablesCollection = executablesMap.values();
      for (List<DeferredExecutable> executablesList: executablesCollection) {
        for (Executable executable: executablesList) {
          e.add(executable);
        }
      }
      return Helpers.first(e);
    }

    static void add(DeferredExecutable executable) {
      String id = executable.delegate.getId();
      if (!executablesMap.containsKey(id))
        executablesMap.put(id, new ArrayList<DeferredExecutable>());
      List<DeferredExecutable> e = executablesMap.get(id);
      e.add(executable);
    }

    static void remove(DeferredExecutable executable) {
      String id = executable.delegate.getId();
      List<DeferredExecutable> e = executablesMap.get(id);
      e.remove(executable);
      if (e.isEmpty())
        executablesMap.remove(id);
    }

  }

  class Helpers {

    static List<Executable> first(List<Executable> executables) {
      Collections.sort(executables);
      List<Executable> first = new ArrayList<Executable>();
      if (!executables.isEmpty())
        first.add(executables.get(0));
      return first;
    }

    static List<Executable> next(ProcessRunnerImpl runner, List instances) {
      List<Executable> executables = new ArrayList<Executable>();
      for (Object instance: instances) {
        Executable executable = instance instanceof Job
            ? Jobs.newInstance(runner, (Job) instance)
            : Waitstates.newInstance(runner, (HistoricActivityInstance) instance);
        if (executable != null)
          executables.add(executable);
      }
      return first(executables);
    }

  }

}


