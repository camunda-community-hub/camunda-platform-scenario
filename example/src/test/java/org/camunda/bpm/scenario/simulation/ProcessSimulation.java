package org.camunda.bpm.scenario.simulation;

import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.act.SendTaskAction;
import org.camunda.bpm.scenario.act.ServiceTaskAction;
import org.camunda.bpm.scenario.act.UserTaskAction;
import org.camunda.bpm.scenario.simulation.finishing.SendTaskFinish;
import org.camunda.bpm.scenario.simulation.finishing.ServiceTaskFinish;
import org.camunda.bpm.scenario.simulation.finishing.UserTaskFinish;
import org.camunda.bpm.scenario.simulation.starting.SendTaskStart;
import org.camunda.bpm.scenario.simulation.starting.ServiceTaskStart;
import org.camunda.bpm.scenario.simulation.starting.UserTaskStart;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class ProcessSimulation implements ProcessScenario {

    @Override
    public ServiceTaskAction waitsAtServiceTask(String activityId) {
        return task -> {
            ServiceTaskStart serviceTaskStart = decidesAboutDeferringServiceTask(task.getActivityId());
            if (serviceTaskStart != null) {
                task.defer(serviceTaskStart.deferal(task),
                    () -> {
                        ServiceTaskFinish serviceTaskFinish = finishesServiceTask(task.getActivityId());
                        if (serviceTaskFinish != null) {
                            task.complete(serviceTaskFinish.variables(task));
                        } else {
                            task.complete();
                        }
                    }
                );
            } else {
                ServiceTaskFinish serviceTaskFinish = finishesServiceTask(task.getActivityId());
                if (serviceTaskFinish != null) {
                    task.complete(serviceTaskFinish.variables(task));
                } else {
                    task.complete();
                }
            }
        };
    }

    @Override
    public SendTaskAction waitsAtSendTask(String activityId) {
        return task -> {
            SendTaskStart sendTaskStart = decidesAboutDeferringSendTask(task.getActivityId());
            if (sendTaskStart != null) {
                task.defer(sendTaskStart.deferal(task),
                    () -> {
                        SendTaskFinish serndTaskFinish = finishesSendTask(task.getActivityId());
                        if (serndTaskFinish != null) {
                            task.complete(serndTaskFinish.variables(task));
                        } else {
                            task.complete();
                        }
                    }
                );
            } else {
                SendTaskFinish serndTaskFinish = finishesSendTask(task.getActivityId());
                if (serndTaskFinish != null) {
                    task.complete(serndTaskFinish.variables(task));
                } else {
                    task.complete();
                }
            }
        };
    }

    @Override
    public UserTaskAction waitsAtUserTask(String activityId) {
        return task -> {
            UserTaskStart userTaskStart = decidesAboutDeferringUserTask(task.getTaskDefinitionKey());
            if (userTaskStart != null) {
                task.defer(userTaskStart.deferal(task),
                        () -> {
                            UserTaskFinish userTaskFinish = finishesUserTask(task.getTaskDefinitionKey());
                            if (userTaskFinish != null) {
                                task.complete(userTaskFinish.variables(task));
                            } else {
                                task.complete();
                            }
                        }
                );
            } else {
                UserTaskFinish userTaskFinish = finishesUserTask(task.getTaskDefinitionKey());
                if (userTaskFinish != null) {
                    task.complete(userTaskFinish.variables(task));
                } else {
                    task.complete();
                }
            }
        };
    }

    public abstract ServiceTaskStart decidesAboutDeferringServiceTask(String activity);

    public abstract SendTaskStart decidesAboutDeferringSendTask(String activity);

    public abstract UserTaskStart decidesAboutDeferringUserTask(String activity);

    public abstract ServiceTaskFinish finishesServiceTask(String activity);

    public abstract SendTaskFinish finishesSendTask(String activity);

    public abstract UserTaskFinish finishesUserTask(String activity);

}
