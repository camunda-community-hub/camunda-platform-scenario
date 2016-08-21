package org.camunda.bpm.scenario.util;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class Supports {

  public static boolean feature(String name) {
    try {
      Class.forName(name);
    } catch (ClassNotFoundException e) {
      return false;
    }
    return true;
  }

}
