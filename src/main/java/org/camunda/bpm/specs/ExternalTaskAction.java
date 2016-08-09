package org.camunda.bpm.specs;

import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.runtime.ProcessInstance;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ExternalTaskAction {

  void execute(ExternalTask externalTask, ProcessInstance processInstance);

}
