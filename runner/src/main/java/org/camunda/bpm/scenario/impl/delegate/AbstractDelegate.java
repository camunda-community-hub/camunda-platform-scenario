package org.camunda.bpm.scenario.impl.delegate;

/**
 * @author Martin Schimak
 */
public abstract class AbstractDelegate<D> {

  protected D delegate;

  public AbstractDelegate(D delegate) {
    this.delegate = delegate;
  }

}
