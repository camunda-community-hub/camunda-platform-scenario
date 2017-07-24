package org.camunda.bpm.simulation;

import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class SendTaskStart {

    private ProcessSimulation simulation;
    private SendTaskDeferral sendTaskDeferral;
    private SendTaskDeferralWithDelegate sendTaskDeferralWithDelegate;

    SendTaskStart(ProcessSimulation simulation) {
        this.simulation = simulation;
    }

    public ProcessSimulation by(final SendTaskDeferral deferral) {
        this.sendTaskDeferral = deferral;
        return simulation;
    }

    public ProcessSimulation by(final SendTaskDeferralWithDelegate deferral) {
        this.sendTaskDeferralWithDelegate = deferral;
        return simulation;
    }

    String deferral(ExternalTaskDelegate task) {
        return sendTaskDeferral != null ? sendTaskDeferral.deferral() : sendTaskDeferralWithDelegate.deferral(task);
    }

    public interface SendTaskDeferral {
        String deferral();
    }

    public interface SendTaskDeferralWithDelegate {
        String deferral(ExternalTaskDelegate task);
    }

}
