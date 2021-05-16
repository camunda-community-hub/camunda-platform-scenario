package org.camunda.bpm.scenario.report.junit;

import org.camunda.bpm.engine.impl.history.HistoryLevel;
import org.camunda.bpm.engine.impl.test.TestHelper;
import org.camunda.bpm.engine.repository.DeploymentBuilder;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.RequiredHistoryLevel;
import org.camunda.bpm.extension.junit5.test.ProcessEngineExtension;
import org.camunda.bpm.scenario.report.bpmn.ProcessScenarioTestReportGenerator;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.function.Supplier;

/**
 * @author Martin Schimak
 */
public class ProcessEngineExtensionWithReporting extends ProcessEngineExtension {

  private void generateProcessScenarioTestReport(ExtensionContext context) {
    context.getTestMethod().ifPresent(method -> {
      Package featurePackage = method.getDeclaringClass().getPackage();
      String featurePackageName = featurePackage != null ? featurePackage.getName() : null;
      new ProcessScenarioTestReportGenerator(
        featurePackageName,
        method.getDeclaringClass().getSimpleName(),
        method.getName()
      ).generate(deploymentId);
    });
  }

  private String deploymentId;

  private static Supplier<IllegalStateException> illegalStateException(String msg) {
    return () -> {
      return new IllegalStateException(msg);
    };
  }

  public void beforeTestExecution(ExtensionContext context) {
    Method testMethod = (Method)context.getTestMethod().orElseThrow(illegalStateException("testMethod not set"));
    Class<?> testClass = (Class)context.getTestClass().orElseThrow(illegalStateException("testClass not set"));
    this.doDeployment(testMethod, testClass);
    this.checkRequiredHistoryLevel(testMethod);
  }

  private void checkRequiredHistoryLevel(Method testMethod) {
    RequiredHistoryLevel annotation = (RequiredHistoryLevel)testMethod.getAnnotation(RequiredHistoryLevel.class);
    if (annotation != null) {
      HistoryLevel currentHistoryLevel = this.getProcessEngineConfiguration().getHistoryLevel();
      String requiredHistoryLevelName = annotation.value();
      int requiredHistoryLevel = 0;
      Iterator var6 = this.getProcessEngineConfiguration().getHistoryLevels().iterator();

      while(var6.hasNext()) {
        HistoryLevel level = (HistoryLevel)var6.next();
        if (level.getName().equalsIgnoreCase(requiredHistoryLevelName)) {
          requiredHistoryLevel = level.getId();
        }
      }

      Assumptions.assumeTrue(currentHistoryLevel.getId() >= requiredHistoryLevel, "ignored because the current history level is too low");
    }

  }

  private void getDeploymentResources(Class<?> testClass, String testMethodName, Deployment annotation, DeploymentBuilder deploymentBuilder) {
    String[] resources = annotation.resources();
    if (resources.length == 0) {
      deploymentBuilder.addClasspathResource(TestHelper.getBpmnProcessDefinitionResource(testClass, testMethodName));
    } else {
      String[] var6 = resources;
      int var7 = resources.length;

      for(int var8 = 0; var8 < var7; ++var8) {
        String resource = var6[var8];
        deploymentBuilder.addClasspathResource(resource);
      }
    }

  }

  private void doDeployment(Method testMethod, Class<?> testClass) {
    DeploymentBuilder deploymentBuilder = this.processEngine.getRepositoryService().createDeployment().name(testClass.getSimpleName() + "." + testMethod.getName());
    Deployment methodAnnotation = (Deployment)testMethod.getAnnotation(Deployment.class);
    if (methodAnnotation != null) {
      this.getDeploymentResources(testClass, testMethod.getName(), methodAnnotation, deploymentBuilder);
      this.deploymentId = deploymentBuilder.deploy().getId();
    } else {
      Deployment classAnnotation = (Deployment)testClass.getAnnotation(Deployment.class);
      if (classAnnotation != null) {
        this.getDeploymentResources(testClass, (String)null, classAnnotation, deploymentBuilder);
        this.deploymentId = deploymentBuilder.deploy().getId();
      } else {
        Class lookForAnnotationClass;
        for(lookForAnnotationClass = testClass.getSuperclass(); lookForAnnotationClass != Object.class; lookForAnnotationClass = lookForAnnotationClass.getSuperclass()) {
          classAnnotation = (Deployment)lookForAnnotationClass.getAnnotation(Deployment.class);
          if (classAnnotation != null) {
            break;
          }
        }

        if (classAnnotation != null) {
          this.getDeploymentResources(lookForAnnotationClass, (String)null, classAnnotation, deploymentBuilder);
          this.deploymentId = deploymentBuilder.deploy().getId();
        }
      }
    }

  }

  public void afterTestExecution(ExtensionContext context) {
    generateProcessScenarioTestReport(context);
    super.afterTestExecution(context);
  }

}
