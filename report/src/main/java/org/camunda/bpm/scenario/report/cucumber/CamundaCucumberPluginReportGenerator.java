package org.camunda.bpm.scenario.report.cucumber;

import io.cucumber.plugin.event.TestCase;
import org.camunda.bpm.scenario.report.bpmn.ProcessScenarioTestReportGenerator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Martin Schimak
 */
public class CamundaCucumberPluginReportGenerator {

  public CamundaCucumberPluginReportGenerator(TestCase testCase, String deploymentId) {

    new ProcessScenarioTestReportGenerator(
      featurePackageName(testCase),
      featureName(testCase),
      scenarioName(testCase)
    ).generate(deploymentId);

  }

  protected String featurePackageName(TestCase testCase) {
    Matcher classPathMatcher = Pattern.compile("classpath\\:(.*)\\/.*?\\.feature")
      .matcher(testCase.getUri().toString());
    if (classPathMatcher.matches()) {
      return classPathMatcher.group(1).replace('/', '.');
    } else {
      Matcher mavenLayoutFilePathMatcher = Pattern.compile("file\\:.*src\\/(main|test)\\/resources\\/(.*)\\/.*?")
        .matcher(testCase.getUri().toString());
      if (mavenLayoutFilePathMatcher.matches()) {
        return mavenLayoutFilePathMatcher.group(2).replace('/', '.');
      } else {
        @SuppressWarnings("OptionalGetWithoutIsPresent")
        String codeLocation = testCase.getTestSteps().stream().findFirst().get().getCodeLocation();
        String[] splitAlongDotSeparator = codeLocation.split("\\.");
        if (splitAlongDotSeparator.length > 2) {
          String firstTwoPackagesRegex = splitAlongDotSeparator[0] + "\\/" + splitAlongDotSeparator[1];
          Matcher firstTwoPackageNamesFilePathMatcher = Pattern.compile(String.format("file\\:.*(%s.*)\\/.*?", firstTwoPackagesRegex))
            .matcher(testCase.getUri().toString());
          if (firstTwoPackageNamesFilePathMatcher.matches())
            return firstTwoPackageNamesFilePathMatcher.group(1).replace('/', '.');
        }
      }
    }
    return null;
  }

  protected String featureName(TestCase testCase) {
    Matcher uriMatcher = Pattern.compile(".*\\/(.*?)\\.feature")
      .matcher(testCase.getUri().toString());
    if (uriMatcher.matches()) {
      return uriMatcher.group(2);
    } else {
      Matcher defaultPackageMatcher = Pattern.compile("classpath\\:(.*?)\\.feature")
        .matcher(testCase.getUri().toString());
      if (defaultPackageMatcher.matches())
        return defaultPackageMatcher.group(1);
    }
    return null;
  }

  protected String scenarioName(TestCase testCase) {
    return testCase.getName();
  }

}
