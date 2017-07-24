package org.camunda.bpm.simulation;

import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ServiceTaskStart {

    private ProcessSimulation simulation;
    private ServiceTaskStart.ServiceTaskDeferral serviceTaskDeferral;
    private ServiceTaskStart.ServiceTaskDeferralWithDelegate serviceTaskDeferralWithDelegate;

    ServiceTaskStart(ProcessSimulation simulation) {
        this.simulation = simulation;
    }

    public ProcessSimulation by(final ServiceTaskStart.ServiceTaskDeferral deferral) {
        this.serviceTaskDeferral = deferral;
        return simulation;
    }

    public ProcessSimulation by(final ServiceTaskStart.ServiceTaskDeferralWithDelegate deferral) {
        this.serviceTaskDeferralWithDelegate = deferral;
        return simulation;
    }

    String deferral(ExternalTaskDelegate task) {
        return serviceTaskDeferral != null ? serviceTaskDeferral.deferral() : serviceTaskDeferralWithDelegate.deferral(task);
    }

    public interface ServiceTaskDeferral {
        String deferral();
    }

    public interface ServiceTaskDeferralWithDelegate {
        String deferral(ExternalTaskDelegate task);
    }

}
