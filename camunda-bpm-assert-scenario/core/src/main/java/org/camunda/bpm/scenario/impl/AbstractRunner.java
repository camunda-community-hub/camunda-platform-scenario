package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.scenario.run.Runner;

import java.util.List;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class AbstractRunner implements Runner {

  public abstract List<Executable> next();

}
