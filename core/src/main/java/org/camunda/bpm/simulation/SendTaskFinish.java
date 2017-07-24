package org.camunda.bpm.simulation;

import org.camunda.bpm.scenario.act.SendTaskAction;
import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class SendTaskFinish {

    private ProcessSimulation simulation;

    private SendTaskAction sendTaskAction;

    SendTaskFinish(ProcessSimulation simulation) {
        this.simulation = simulation;
    }

    public ProcessSimulation with(final SendTaskFinish.SendTaskVariables variables) {
        return with(new SendTaskAction() {
            @Override
            public void execute(ExternalTaskDelegate task) throws Exception {
                Map<String, Object> map;
                map = variables != null ? variables.variables() : null;
                if (map != null) {
                    task.complete(map);
                } else {
                    task.complete();
                }
            }
        });
    }

    public ProcessSimulation with(final SendTaskAction action) {
        this.sendTaskAction = action;
        return simulation;
    }

    SendTaskAction action() {
        return sendTaskAction;
    }

    public interface SendTaskVariables {
        Map<String, Object> variables();
    }

}
