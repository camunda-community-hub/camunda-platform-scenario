package org.camunda.bpm.scenario.impl.util;

/**
 * @author <a href="martin.schimak@plexiti.com">Martin Schimak</a>
 */
public class Strings {

  public static String leftpad(String text, int length) {
    return String.format("%" + length + "." + length + "s", text);
  }

  public static String rightpad(String text, int length) {
    return String.format("%-" + length + "." + length + "s", text);
  }

  public static String trimAll(String text) {
    return text == null ? "" : text.replaceAll("\\s+", " ").trim();
  }

}
