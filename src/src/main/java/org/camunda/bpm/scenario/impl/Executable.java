package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.impl.persistence.entity.JobEntity;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.scenario.impl.job.IgnoredJob;
import org.camunda.bpm.scenario.impl.waitstate.IgnoredWaitstate;

import java.util.HashMap;
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

    static ExecutableWaitstate newInstance(ProcessRunnerImpl runner, HistoricActivityInstance instance, String duration) {
      String type = instance.getActivityType();
      if (types.containsKey(type)) {
        try {
          return (ExecutableWaitstate) Class.forName(IgnoredWaitstate.class.getPackage().getName() + "." + types.get(type)).getConstructor(ProcessRunnerImpl.class, HistoricActivityInstance.class, String.class).newInstance(runner, instance, duration);
        } catch (Exception e) {
          throw new IllegalArgumentException(e);
        }
      }
      return new IgnoredWaitstate(runner, instance, duration);
    }

  }

  class Jobs {

    static Map<String, String> types = new HashMap<String, String>(); static {
      types.put("async-continuation", "ExecutableContinuation");
      types.put("timer-transition", "ExecutableTimerJob");
      types.put("timer-start-event-subprocess", "ExecutableTimerJob");
    }

    static ExecutableJob newInstance(ProcessRunnerImpl runner, Job job) {
      JobEntity entity = (JobEntity) job;
      String type = entity.getJobHandlerType();
      if (types.containsKey(type)) {
        try {
          return (ExecutableJob) Class.forName(IgnoredJob.class.getPackage().getName() + "." + types.get(type)).getConstructor(ProcessRunnerImpl.class, Job.class).newInstance(runner, job);
        } catch (Exception e) {
          throw new IllegalArgumentException(e);
        }
      }
      return new IgnoredJob(runner, job);
    }

  }

}


