package org.camunda.bpm.scenario.runner;

import org.camunda.bpm.engine.runtime.Job;

import java.util.Date;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ScenarioRunner<R> {

  R run();

  Waitstate nextWaitstate(boolean lastCall);

  Job nextTimerUntil(Date endTime);

}
