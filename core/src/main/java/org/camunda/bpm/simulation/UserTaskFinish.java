package org.camunda.bpm.simulation;

import org.camunda.bpm.scenario.act.UserTaskAction;
import org.camunda.bpm.scenario.delegate.TaskDelegate;

import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class UserTaskFinish {

    private ProcessSimulation simulation;

    private UserTaskAction userTaskAction;

    UserTaskFinish(ProcessSimulation simulation) {
        this.simulation = simulation;
    }

    public ProcessSimulation with(final UserTaskVariables variables) {
        return with(new UserTaskAction() {
            @Override
            public void execute(TaskDelegate task) throws Exception {
                Map<String, Object> map = variables != null ? variables.variables() : null;
                if (map != null) {
                    task.complete(map);
                } else {
                    task.complete();
                }
            }
        });
    }

    public ProcessSimulation with(final UserTaskAction action) {
        this.userTaskAction = action;
        return simulation;
    }

    UserTaskAction action() {
        return userTaskAction;
    }

    public interface UserTaskVariables {
        Map<String, Object> variables();
    }

}
