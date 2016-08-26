package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.runtime.Job;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ScenarioRunner<R> {

  R run();

  Waitstate next();

  Job next(Waitstate waitstate);

  void finish();

}
