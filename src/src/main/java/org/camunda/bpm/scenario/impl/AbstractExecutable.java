package org.camunda.bpm.scenario.impl;

import org.camunda.bpm.scenario.impl.delegate.AbstractProcessEngineServicesDelegate;
import org.camunda.bpm.scenario.impl.util.IdComparator;

import java.util.Date;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class AbstractExecutable<I> extends AbstractProcessEngineServicesDelegate implements Executable<AbstractExecutable> {

  protected static IdComparator idComparator = new IdComparator();

  protected ProcessRunnerImpl runner;
  protected I delegate;

  protected AbstractExecutable(ProcessRunnerImpl runner) {
    super(runner.scenarioExecutor.processEngine);
    this.runner = runner;
  }

  public abstract String getExecutionId();

  protected abstract I getDelegate();

  protected abstract Date isExecutableAt();

  @Override
  public int compareTo(AbstractExecutable other) {
    assert other != null;
    int compared = isExecutableAt().compareTo(other.isExecutableAt());
    if (compared == 0) {
      if (this.getClass().equals(other.getClass())) {
        return 0;
      } else if (other instanceof JobExecutable) {
        return -1;
      } else {
        return other instanceof WaitstateExecutable ? -1 : 1;
      }
    } else {
      return compared;
    }
  }

}
