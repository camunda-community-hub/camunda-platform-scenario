package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.engine.runtime.Job;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface Runner<R> {

  R run();

  Executable next();

  Job next(ExecutableWaitstate waitstate);

  void finish();

}
