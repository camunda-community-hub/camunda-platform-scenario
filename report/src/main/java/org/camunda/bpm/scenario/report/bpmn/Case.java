package org.camunda.bpm.scenario.report.bpmn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Martin Schimak
 */
public enum Case {

  CAMEL("camel"), SNAKE("snake"), KEBAB("kebab");

  private String key;

  Case(String key) {
    this.key = key;
  }

  public String key() {
    return key;
  }

  public static Case valueOfKey(String key) {
    for (Case e : values()) {
      if (e.key.equals(key)) {
        return e;
      }
    }
    if (key == null || key.equals(""))
      return CAMEL;
    throw new IllegalStateException(String.format("Unknown case '%s': use one of %s", key, Arrays.toString(Arrays.stream(values()).map(v -> v.key).toArray())));
  }

  public String separator() {
    switch (this) {
      case CAMEL: return "";
      case SNAKE: return "_";
      case KEBAB: return "-";
      default: throw new IllegalStateException();
    }
  }

  public String convert(String input) {
    switch (this) {
      case CAMEL: return camel(input);
      case SNAKE: return snake(input);
      case KEBAB: return kebab(input);
      default: throw new IllegalStateException();
    }
  }

  private static String kebab(String input) {
    Matcher matcher = Pattern.compile("[A-Z]{2,}(?=[A-Z][a-z]+[0-9]*|\\b)|[A-Z]?[a-z]+[0-9]*|[A-Z]|[0-9]+").matcher(input);
    List< String > matched = new ArrayList< >();
    while (matcher.find()) {
      matched.add(matcher.group(0));
    }
    return matched.stream()
      .map(String::toLowerCase)
      .collect(Collectors.joining("-"));
  }

  public static String snake(String input) {
    Matcher matcher = Pattern.compile("[A-Z]{2,}(?=[A-Z][a-z]+[0-9]*|\\b)|[A-Z]?[a-z]+[0-9]*|[A-Z]|[0-9]+").matcher(input);
    List < String > matched = new ArrayList < > ();
    while (matcher.find()) {
      matched.add(matcher.group(0));
    }
    return matched.stream()
      .map(String::toLowerCase)
      .collect(Collectors.joining("_"));
  }

  public static String camel(String str) {
    Matcher matcher = Pattern.compile("[A-Z]{2,}(?=[A-Z][a-z]+[0-9]*|\\b)|[A-Z]?[a-z]+[0-9]*|[A-Z]|[0-9]+").matcher(str);
    List < String > matched = new ArrayList<>();
    while (matcher.find()) {
      matched.add(matcher.group(0));
    }
    return matched.stream()
      .map(x -> x.substring(0, 1).toUpperCase() + x.substring(1).toLowerCase())
      .collect(Collectors.joining());
  }

}
