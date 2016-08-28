package org.camunda.bpm.scenario.impl.delegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public abstract class AbstractDelegate<D> {

  protected D delegate;

  public AbstractDelegate(D delegate) {
    this.delegate = delegate;
  }

}
