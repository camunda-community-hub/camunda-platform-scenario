package org.camunda.bpm.scenario.report.bpmn;

import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.scenario.report.Report;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Martin Schimak
 */
public class ProcessScenarioTestReportGenerator extends AbstractProcessReport<ProcessScenarioTestReportGenerator> {

  private static final Properties PROPERTIES = new Properties();

  private static final String RESOURCE_PROPERTIES_FILE_NAME = "camunda-platform-scenario.properties";
  private static final String SCENARIO_REPORTS_PATH = "scenario.reports.path";
  private static final String SCENARIO_REPORTS_FILE_NAME_CASE = "scenario.reports.file.name.case";
  private static final String COVERAGE_REPORTS_PATH = "coverage.reports.path";
  private static final String COVERAGE_REPORTS_FILE_NAME_CASE = "coverage.reports.file.name.case";

  private static final String DEFAULT_REPORTS_PATH = "./target/camunda-reports/";
  private static final String SCENARIO = "scenario";
  private static final String COVERAGE = "coverage";

  static {

    // default properties
    PROPERTIES.setProperty(SCENARIO_REPORTS_PATH, DEFAULT_REPORTS_PATH + SCENARIO);
    PROPERTIES.setProperty(SCENARIO_REPORTS_FILE_NAME_CASE, Case.camel.name());
    PROPERTIES.setProperty(COVERAGE_REPORTS_PATH, DEFAULT_REPORTS_PATH + COVERAGE);
    PROPERTIES.setProperty(COVERAGE_REPORTS_FILE_NAME_CASE, Case.camel.name());

    // camunda-platform-scenario.properties
    InputStream resourcePropertiesStream = ProcessScenarioTestReportGenerator.class
      .getResourceAsStream("/" + RESOURCE_PROPERTIES_FILE_NAME);
    if (resourcePropertiesStream != null) {
      try {
        Properties resourceProperties = new Properties();
        resourceProperties.load(resourcePropertiesStream);
        Enumeration<?> properties = PROPERTIES.propertyNames();
        while (properties.hasMoreElements()) {
          String propertyName = (String) properties.nextElement();
          String resourceProperty = resourceProperties.getProperty(propertyName);
          if (resourceProperty != null && resourceProperty.length() > 0)
            PROPERTIES.setProperty(propertyName, resourceProperty);
        }
      } catch (IOException exception) {
        throw new RuntimeException(exception);
      }
    }

    // system properties
    Enumeration<?> properties = PROPERTIES.propertyNames();
    while (properties.hasMoreElements()) {
      String propertyName = (String) properties.nextElement();
      String systemProperty = System.getProperty(propertyName);
      if (systemProperty != null && systemProperty.length() > 0)
        PROPERTIES.setProperty(propertyName, systemProperty);
    }

  }

  private final String scenarioReportsPath;
  private final String coverageReportsPath;
  private final String featurePackageName;
  private final String featureName;
  private final String scenarioName;

  private final Case scenarioReportsFileNameCase = Case.valueOf(PROPERTIES.getProperty(SCENARIO_REPORTS_FILE_NAME_CASE));
  private final Case coverageReportsFileNameCase = Case.valueOf(PROPERTIES.getProperty(COVERAGE_REPORTS_FILE_NAME_CASE));

  public ProcessScenarioTestReportGenerator(
    String featurePackageName,
    String featureName,
    String scenarioName) {
    this(
      featurePackageName,
      featureName,
      scenarioName,
      PROPERTIES.getProperty(SCENARIO_REPORTS_PATH),
      PROPERTIES.getProperty(COVERAGE_REPORTS_PATH)
    );
  }

  public ProcessScenarioTestReportGenerator(
    String featurePackageName,
    String featureName,
    String scenarioName,
    String reportsPath) {
    this(
      featurePackageName,
      featureName,
      scenarioName,
      Paths.get(reportsPath, SCENARIO).toString(),
      Paths.get(reportsPath, COVERAGE).toString()
    );
  }

  public ProcessScenarioTestReportGenerator(
    String featurePackageName,
    String featureName,
    String scenarioName,
    String scenarioReportsPath,
    String coverageReportsPath) {
    this.featurePackageName = featurePackageName != null && featurePackageName.length() > 0 ? featurePackageName : "";
    this.featureName = featureName;
    this.scenarioName = scenarioName;
    this.scenarioReportsPath = scenarioReportsPath;
    this.coverageReportsPath = coverageReportsPath;
  }

  private static final Pattern bpmnResourceNamePattern = Pattern.compile("((.+)\\/)?(.+?)\\.bpmn");

  @Override
  public ProcessScenarioTestReportGenerator generate(String deploymentId) {

    List<ProcessDefinition> processDefinitions =
      processEngine.getRepositoryService().createProcessDefinitionQuery()
        .deploymentId(deploymentId).list();

    for (ProcessDefinition processDefinition : processDefinitions) {

      List<HistoricProcessInstance> processInstances =
        processEngine.getHistoryService().createHistoricProcessInstanceQuery()
          .processDefinitionId(processDefinition.getId()).list();

      final int total = processInstances.size();

      if (total > 0) {

        Matcher bpmnModelNameMatcher = bpmnResourceNamePattern.matcher(processDefinition.getResourceName());
        if (!bpmnModelNameMatcher.matches())
          throw new IllegalStateException(String.format("Cannot parse bpmn resource name %s", processDefinition.getResourceName()));
        String bpmnModelPackageName =
          bpmnModelNameMatcher.group(2) != null ? bpmnModelNameMatcher.group(2).replace('/', '.') : "";
        String bpmnModelSimpleName = bpmnModelNameMatcher.group(3);

        String scenarioReportFeatureName = scenarioReportsFileNameCase.from(featureName);
        String scenarioReportScenarioName = scenarioReportsFileNameCase.from(scenarioName);

        String scenarioReportFolder = Paths.get(
          scenarioReportsPath,
          featurePackageName,
          scenarioReportFeatureName,
          scenarioReportScenarioName
        ).toString();

        for (int i = 0; i < total; i++) {

          String bpmnModelScenarioName = String.format("%s%s%s.bpmn",
            scenarioReportsFileNameCase.from(bpmnModelSimpleName),
            scenarioReportsFileNameCase.separator,
            scenarioReportsFileNameCase.from(
              String.format("%s%s%s%s", SCENARIO,
                (total == 1) ? "" : scenarioReportsFileNameCase.separator + (i + 1),
                scenarioReportsFileNameCase.separator,
                scenarioReportScenarioName)
            ));

          Path scenarioReportFile = Paths.get(scenarioReportFolder, bpmnModelScenarioName);
          BpmnModelInstance scenarioReportModel = Report.processScenarioReport().generate(processInstances.get(i).getId());

          writeReport(scenarioReportFile, scenarioReportModel);

        }

        String coverageReportFolder = Paths.get(
          coverageReportsPath,
          bpmnModelPackageName
        ).toString();

        String bpmnModelCoverageName = String.format("%s%s%s.bpmn",
          coverageReportsFileNameCase.from(bpmnModelSimpleName),
          coverageReportsFileNameCase.separator,
          coverageReportsFileNameCase.from(COVERAGE)
        );

        Path coverageReportFile = Paths.get(coverageReportFolder, bpmnModelCoverageName);
        BpmnModelInstance coverageReport = Report.processCoverageReport().generate(processDefinition.getKey());
        writeReport(coverageReportFile, coverageReport);

      }

    }

    return this;

  }

  private void writeReport(Path path, BpmnModelInstance report) {
    try {
      Files.createDirectories(path.getParent());
      Bpmn.writeModelToFile(path.toFile(), report);
    } catch (IOException exception) {
      throw new RuntimeException(exception);
    }
  }

}
