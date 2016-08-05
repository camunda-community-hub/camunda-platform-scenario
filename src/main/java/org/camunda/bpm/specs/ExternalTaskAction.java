package org.camunda.bpm.specs;

import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.runtime.ProcessInstance;

/**
 * Created by martin on 05.08.16.
 */
public interface ExternalTaskAction {

  void execute(ExternalTask externalTask, ProcessInstance processInstance);

}
