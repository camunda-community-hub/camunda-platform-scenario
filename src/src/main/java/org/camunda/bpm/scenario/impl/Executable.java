package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.impl.persistence.entity.JobEntity;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.scenario.action.DeferredAction;
import org.camunda.bpm.scenario.impl.job.ExecutableContinuation;
import org.camunda.bpm.scenario.impl.waitstate.IgnoredWaitstate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface Executable<S> extends Comparable<S> {

  void execute();

  class Waitstates {

    static Map<String, String> types = new HashMap<String, String>(); static {
      types.put("userTask", "UserTaskWaitstate");
      types.put("intermediateSignalCatch", "SignalIntermediateCatchEventWaitstate");
      types.put("intermediateMessageCatch", "MessageIntermediateCatchEventWaitstate");
      types.put("receiveTask", "ReceiveTaskWaitstate");
      types.put("intermediateTimer", "TimerIntermediateEventWaitstate");
      types.put("eventBasedGateway", "EventBasedGatewayWaitstate");
      types.put("callActivity", "CallActivityWaitstate");
      types.put("serviceTask", "ServiceTaskWaitstate");
      types.put("sendTask", "SendTaskWaitstate");
      types.put("intermediateMessageThrowEvent", "MessageIntermediateThrowEventWaitstate");
    }

    static ExecutableWaitstate newInstance(ProcessRunnerImpl runner, HistoricActivityInstance instance) {
      if (!runner.unavailable.contains(instance.getId())) {
        String type = instance.getActivityType();
        if (types.containsKey(type)) {
          try {
            return (ExecutableWaitstate) Class.forName(IgnoredWaitstate.class.getPackage().getName() + "." + types.get(type)).getConstructor(ProcessRunnerImpl.class, HistoricActivityInstance.class).newInstance(runner, instance);
          } catch (Exception e) {
            throw new IllegalArgumentException(e);
          }
        }
        return new IgnoredWaitstate(runner, instance);
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
      types.put("async-continuation", "ExecutableContinuation");
      types.put("timer-transition", "ExecutableTimerJob");
      types.put("timer-intermediate-transition", "ExecutableTimerJob");
      types.put("timer-start-event-subprocess", "ExecutableTimerJob");
    }

    static ExecutableJob newInstance(ProcessRunnerImpl runner, Job job) {
      JobEntity entity = (JobEntity) job;
      String type = entity.getJobHandlerType();
      if (types.containsKey(type)) {
        try {
          return (ExecutableJob) Class.forName(ExecutableContinuation.class.getPackage().getName() + "." + types.get(type)).getConstructor(ProcessRunnerImpl.class, Job.class).newInstance(runner, job);
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

  class Deferred {

    public static DeferredExecutable newInstance(ProcessRunnerImpl runner, HistoricActivityInstance instance, String period, DeferredAction action) {
      return new DeferredExecutable(runner, instance, period, action);
    }

    static List<Executable> next(ProcessRunnerImpl runner) {
      List<Executable> executables = new ArrayList<Executable>();
      Collection<List<DeferredExecutable>> executablesCollection = runner.deferredExecutables.values();
      for (List<DeferredExecutable> executablesList: executablesCollection) {
        for (Executable executable: executablesList) {
          executables.add(executable);
        }
      }
      return Helpers.first(executables);
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


