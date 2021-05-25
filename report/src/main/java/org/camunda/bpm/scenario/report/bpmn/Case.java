package org.camunda.bpm.scenario.report.bpmn;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Martin Schimak
 */
enum Case {

  camel(""), snake("_"), kebab("-");

  public final String separator;

  Case(String separator) {
    this.separator = separator;
  }

  private static final Pattern pattern =
    Pattern.compile("[A-Z]{2,}(?=[A-Z][a-z]+[0-9]*|\\b)|[A-Z]?[a-z]+[0-9]*|[A-Z]|[0-9]+");

  public String from(String input) {
    Matcher matcher = pattern.matcher(input);
    List <String> matched = new ArrayList<>();
    while (matcher.find()) {
      matched.add(matcher.group(0));
    }
    String output = matched.stream()
      .map(x -> x.substring(0, 1).toUpperCase() + x.substring(1).toLowerCase())
      .collect(Collectors.joining(separator));
    return equals(camel) ? output : output.toLowerCase();
  }

}
