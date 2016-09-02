package org.camunda.bpm.scenario.impl;

import java.util.List;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface Runner {

  List<Executable> next();

}
