package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.impl.persistence.entity.JobEntity;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.scenario.defer.Deferred;
import org.camunda.bpm.scenario.impl.job.ContinuationExecutable;
import org.camunda.bpm.scenario.impl.waitstate.IgnoredExecutable;

import java.util.*;

/**
 * @author Martin Schimak
 */
@SuppressWarnings("unchecked")
public interface Executable<S> extends Comparable<S> {

  void execute();

  class Waitstates {

    static Map<String, String> types = new HashMap<String, String>();

    static {
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

    static WaitstateExecutable newInstance(ProcessInstanceRunner runner, HistoricActivityInstance instance) {
      if (!runner.isExecuted(instance)) {
        String type = instance.getActivityType();
        if (types.containsKey(type)) {
          try {
            return (WaitstateExecutable) Class.forName(IgnoredExecutable.class.getPackage().getName() + "." + types.get(type)).getConstructor(ProcessInstanceRunner.class, HistoricActivityInstance.class).newInstance(runner, instance);
          } catch (Exception e) {
            throw new IllegalArgumentException(e);
          }
        }
        return new IgnoredExecutable(runner, instance);
      }
      return null;
    }

    static List<Executable> next(ProcessInstanceRunner runner) {
      List<HistoricActivityInstance> instances = runner.scenarioRunner.processEngine
        .getHistoryService().createHistoricActivityInstanceQuery()
        .processInstanceId(runner.processInstance.getId()).unfinished().list();
      return Helpers.next(runner, instances);
    }

  }

  class Jobs {

    static Map<String, String> types = new HashMap<String, String>();

    static {
      types.put("async-continuation", "ContinuationExecutable");
      types.put("timer-transition", "TimerJobExecutable");
      types.put("timer-intermediate-transition", "TimerJobExecutable");
      types.put("timer-start-event-subprocess", "TimerJobExecutable");
    }

    static JobExecutable newInstance(ProcessInstanceRunner runner, Job job) {
      JobEntity entity = (JobEntity) job;
      String type = entity.getJobHandlerType();
      if (types.containsKey(type)) {
        try {
          return (JobExecutable) Class.forName(ContinuationExecutable.class.getPackage().getName() + "." + types.get(type)).getConstructor(ProcessInstanceRunner.class, Job.class).newInstance(runner, job);
        } catch (Exception e) {
          throw new IllegalArgumentException(e);
        }
      }
      return null;
    }

    static List<Executable> next(ProcessInstanceRunner runner) {
      List<Job> jobs = runner.scenarioRunner.processEngine.getManagementService()
        .createJobQuery().processInstanceId(runner.processInstance.getId()).list();
      return Helpers.next(runner, jobs);
    }

  }

  class Deferreds {

    private static final Map<String, List<DeferredExecutable>> executablesMap = new HashMap<String, List<DeferredExecutable>>();

    public static DeferredExecutable newInstance(ProcessInstanceRunner runner, HistoricActivityInstance instance, String period, Deferred action) {
      return new DeferredExecutable(runner, instance, period, action);
    }

    static List<Executable> next(ProcessInstanceRunner runner) {
      List<Executable> e = new ArrayList<Executable>();
      Collection<List<DeferredExecutable>> executablesCollection = executablesMap.values();
      for (List<DeferredExecutable> executablesList : executablesCollection) {
        for (Executable executable : executablesList) {
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

    static List<Executable> next(ProcessInstanceRunner runner, List instances) {
      List<Executable> executables = new ArrayList<Executable>();
      for (Object instance : instances) {
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


