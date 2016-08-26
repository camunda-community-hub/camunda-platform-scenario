package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.runtime.Job;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ScenarioRunner<R> {

  R run();

  AbstractWaitstate next();

  Job next(AbstractWaitstate waitstate);

  void finish();

}
