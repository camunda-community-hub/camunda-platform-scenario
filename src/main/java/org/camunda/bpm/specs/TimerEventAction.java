package org.camunda.bpm.specs;

import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.ProcessInstance;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface TimerEventAction {

  void execute(Job timerJob, ProcessInstance processInstance);

}
