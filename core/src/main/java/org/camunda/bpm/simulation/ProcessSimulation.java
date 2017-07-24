package org.camunda.bpm.simulation;

import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.act.BusinessRuleTaskAction;
import org.camunda.bpm.scenario.act.ConditionalIntermediateEventAction;
import org.camunda.bpm.scenario.act.EventBasedGatewayAction;
import org.camunda.bpm.scenario.act.MessageEndEventAction;
import org.camunda.bpm.scenario.act.MessageIntermediateCatchEventAction;
import org.camunda.bpm.scenario.act.MessageIntermediateThrowEventAction;
import org.camunda.bpm.scenario.act.ReceiveTaskAction;
import org.camunda.bpm.scenario.act.SendTaskAction;
import org.camunda.bpm.scenario.act.ServiceTaskAction;
import org.camunda.bpm.scenario.act.SignalIntermediateCatchEventAction;
import org.camunda.bpm.scenario.act.TimerIntermediateEventAction;
import org.camunda.bpm.scenario.act.UserTaskAction;
import org.camunda.bpm.scenario.defer.Deferred;
import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;
import org.camunda.bpm.scenario.delegate.TaskDelegate;
import org.camunda.bpm.scenario.run.Runner;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ProcessSimulation implements StartableSimulator, ExecutableSimulator {

    private SimulationScenario simulationScenario = new SimulationScenario();
    
    private String processDefinitionKey;

    private ServiceTaskStart serviceTaskStart;

    private SendTaskStart sendTaskStart;

    private UserTaskStart userTaskStart;
    
    private Map<String, ServiceTaskStart> serviceTaskStarts = new HashMap<String, ServiceTaskStart>();

    private Map<String, SendTaskStart> sendTaskStarts = new HashMap<String, SendTaskStart>();

    private Map<String, UserTaskStart> userTaskStarts = new HashMap<String, UserTaskStart>();

    private Map<String, ServiceTaskFinish> serviceTaskFinish = new HashMap<String, ServiceTaskFinish>();

    private Map<String, SendTaskFinish> sendTaskFinish = new HashMap<String, SendTaskFinish>();

    private Map<String, UserTaskFinish> userTaskFinish = new HashMap<String, UserTaskFinish>();

    @Override
    public ExecutableSimulator startByKey(String processDefinitionKey) {
        this.processDefinitionKey = processDefinitionKey;
        return this;
    }

    @Override
    public void execute(int times) {
        for (int t = 0; t < times; t++) {
            Scenario.run(simulationScenario).startByKey(processDefinitionKey).execute();
        }
    }

    public ServiceTaskStart deferServiceTask() {
        return serviceTaskStart = new ServiceTaskStart(this);
    }

    public SendTaskStart deferSendTask() {
        return sendTaskStart = new SendTaskStart(this);
    }

    public UserTaskStart deferUserTask() {
        return userTaskStart = new UserTaskStart(this);
    }

    public ServiceTaskStart deferServiceTask(String activity) {
        serviceTaskStarts.put(activity, new ServiceTaskStart(this));
        return serviceTaskStarts.get(activity);
    }

    public SendTaskStart deferSendTask(String activity) {
        sendTaskStarts.put(activity, new SendTaskStart(this));
        return sendTaskStarts.get(activity);
    }

    public UserTaskStart deferUserTask(String activity) {
        userTaskStarts.put(activity, new UserTaskStart(this));
        return userTaskStarts.get(activity);
    }

    public ServiceTaskFinish finishServiceTask(String activity) {
        serviceTaskFinish.put(activity, new ServiceTaskFinish(this));
        return serviceTaskFinish.get(activity);
    }

    public SendTaskFinish finishesSendTask(String activity) {
        sendTaskFinish.put(activity, new SendTaskFinish(this));
        return sendTaskFinish.get(activity);
    }

    public UserTaskFinish finishUserTask(String activity) {
        userTaskFinish.put(activity, new UserTaskFinish(this));
        return userTaskFinish.get(activity);
    }

    private class SimulationScenario implements ProcessScenario {

        @Override
        public UserTaskAction waitsAtUserTask(final String activityId) {
            return new UserTaskAction() {
                @Override
                public void execute(final TaskDelegate task) throws Exception {
                    UserTaskStart userTaskStart = ProcessSimulation.this.userTaskStarts.get(activityId);
                    userTaskStart = userTaskStart != null ? userTaskStart : ProcessSimulation.this.userTaskStart;
                    final UserTaskFinish userTaskFinish = ProcessSimulation.this.userTaskFinish.get(activityId);
                    if (userTaskStart != null) {
                        task.defer(userTaskStart.deferral(task), new Deferred() {
                            @Override
                            public void execute() throws Exception {
                                if (userTaskFinish != null) {
                                    userTaskFinish.action().execute(task);
                                } else {
                                    task.complete();
                                }
                            }
                        });
                    } else if (userTaskFinish != null) {
                        userTaskFinish.action().execute(task);
                    } else {
                        task.complete();
                    }
                }
            };
        }

        @Override
        public TimerIntermediateEventAction waitsAtTimerIntermediateEvent(String activityId) {
            return null;
        }

        @Override
        public MessageIntermediateCatchEventAction waitsAtMessageIntermediateCatchEvent(String activityId) {
            return null;
        }

        @Override
        public ReceiveTaskAction waitsAtReceiveTask(String activityId) {
            return null;
        }

        @Override
        public SignalIntermediateCatchEventAction waitsAtSignalIntermediateCatchEvent(String activityId) {
            return null;
        }

        @Override
        public Runner runsCallActivity(String activityId) {
            return null;
        }

        @Override
        public EventBasedGatewayAction waitsAtEventBasedGateway(String activityId) {
            return null;
        }

        @Override
        public ServiceTaskAction waitsAtServiceTask(final String activityId) {
            return new ServiceTaskAction() {
                @Override
                public void execute(final ExternalTaskDelegate task) throws Exception {
                    ServiceTaskStart serviceTaskStart = ProcessSimulation.this.serviceTaskStarts.get(activityId);
                    serviceTaskStart = serviceTaskStart != null ? serviceTaskStart : ProcessSimulation.this.serviceTaskStart;
                    final ServiceTaskFinish serviceTaskFinish = ProcessSimulation.this.serviceTaskFinish.get(activityId);
                    if (serviceTaskStart != null) {
                        task.defer(serviceTaskStart.deferral(task), new Deferred() {
                            @Override
                            public void execute() throws Exception {
                                if (serviceTaskFinish != null) {
                                    serviceTaskFinish.action().execute(task);
                                } else {
                                    task.complete();
                                }
                            }
                        });
                    } else if (serviceTaskFinish != null) {
                        serviceTaskFinish.action().execute(task);
                    } else {
                        task.complete();
                    }
                }
            };
        }

        @Override
        public SendTaskAction waitsAtSendTask(final String activityId) {
            return new SendTaskAction() {
                @Override
                public void execute(final ExternalTaskDelegate task) throws Exception {
                    SendTaskStart sendTaskStart = ProcessSimulation.this.sendTaskStarts.get(activityId);
                    sendTaskStart = sendTaskStart != null ? sendTaskStart : ProcessSimulation.this.sendTaskStart;
                    final SendTaskFinish sendTaskFinish = ProcessSimulation.this.sendTaskFinish.get(activityId);
                    if (sendTaskStart != null) {
                        task.defer(sendTaskStart.deferral(task), new Deferred() {
                            @Override
                            public void execute() throws Exception {
                                if (sendTaskFinish != null) {
                                    sendTaskFinish.action().execute(task);
                                } else {
                                    task.complete();
                                }
                            }
                        });
                    } else if (sendTaskFinish != null) {
                        sendTaskFinish.action().execute(task);
                    } else {
                        task.complete();
                    }
                }
            };
        }

        @Override
        public MessageIntermediateThrowEventAction waitsAtMessageIntermediateThrowEvent(String activityId) {
            return null;
        }

        @Override
        public MessageEndEventAction waitsAtMessageEndEvent(String activityId) {
            return null;
        }

        @Override
        public BusinessRuleTaskAction waitsAtBusinessRuleTask(String activityId) {
            return null;
        }

        @Override
        public ConditionalIntermediateEventAction waitsAtConditionalIntermediateEvent(String activityId) {
            return null;
        }

        @Override
        public void hasStarted(String activityId) {

        }

        @Override
        public void hasFinished(String activityId) {

        }

        @Override
        public void hasCompleted(String activityId) {

        }

        @Override
        public void hasCanceled(String activityId) {

        }

    }

}
