package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.scenario.run.Runner;

import java.util.List;

/**
 * @author <a href="martin.schimak@plexiti.com">Martin Schimak</a>
 */
public abstract class AbstractRunner implements Runner {

  public abstract List<Executable> next();

}
