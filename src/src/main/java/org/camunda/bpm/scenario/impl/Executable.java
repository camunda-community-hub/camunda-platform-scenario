package org.camunda.bpm.scenario.impl;

import java.util.Date;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface Executable extends Comparable<Executable> {

  void execute();

  Date isExecutableAt();

}
