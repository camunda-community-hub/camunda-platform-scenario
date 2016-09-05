package org.camunda.bpm.scenario.impl.util;

import java.util.Comparator;
import java.util.UUID;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class IdComparator implements Comparator<String> {

  @Override
  public int compare(String thisId, String otherId) {
    String string1 = thisId.substring(thisId.lastIndexOf(':') + 1);
    String string2 = otherId.substring(otherId.lastIndexOf(':') + 1);
    try {
      Long long1 = Long.parseLong(string1);
      Long long2 = Long.parseLong(string2);
      return long1.compareTo(long2);
    } catch (NumberFormatException e1) {
      try {
        return UUID.fromString(string1).compareTo(UUID.fromString(string2));
      } catch (IllegalArgumentException e2) {
        throw new IllegalArgumentException("You seem to use an unsupported ID generator.", e2);
      }
    }
  }
  
}
