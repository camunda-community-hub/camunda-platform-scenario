package org.camunda.bpm.simulation;

import org.camunda.bpm.scenario.act.ServiceTaskAction;
import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ServiceTaskFinish {

    private ProcessSimulation simulation;

    private ServiceTaskAction serviceTaskAction;

    ServiceTaskFinish(ProcessSimulation simulation) {
        this.simulation = simulation;
    }

    public ProcessSimulation with(final ServiceTaskVariables variables) {
        return with(new ServiceTaskAction() {
            @Override
            public void execute(ExternalTaskDelegate task) throws Exception {
                Map<String, Object> map = variables != null ? variables.variables() : null;
                if (map != null) {
                    task.complete(map);
                } else {
                    task.complete();
                }
            }
        });
    }

    public ProcessSimulation with(final ServiceTaskAction action) {
        this.serviceTaskAction = action;
        return simulation;
    }

    ServiceTaskAction action() {
        return serviceTaskAction;
    }

    public interface ServiceTaskVariables {
        Map<String, Object> variables();
    }

}
