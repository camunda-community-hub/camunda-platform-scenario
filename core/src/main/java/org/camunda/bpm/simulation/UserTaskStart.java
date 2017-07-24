package org.camunda.bpm.simulation;

import org.camunda.bpm.scenario.delegate.TaskDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class UserTaskStart {

    private ProcessSimulation simulation;
    private UserTaskStart.UserTaskDeferral userTaskDeferral;
    private UserTaskStart.UserTaskDeferralWithDelegate userTaskDeferralWithDelegate;

    UserTaskStart(ProcessSimulation simulation) {
        this.simulation = simulation;
    }

    public ProcessSimulation by(final UserTaskStart.UserTaskDeferral deferral) {
        this.userTaskDeferral = deferral;
        return simulation;
    }

    public ProcessSimulation by(final UserTaskStart.UserTaskDeferralWithDelegate deferral) {
        this.userTaskDeferralWithDelegate = deferral;
        return simulation;
    }

    String deferral(TaskDelegate task) {
        return userTaskDeferral != null ? userTaskDeferral.deferral() : userTaskDeferralWithDelegate.deferral(task);
    }

    public interface UserTaskDeferral {
        String deferral();
    }

    public interface UserTaskDeferralWithDelegate {
        String deferral(TaskDelegate task);
    }

}
