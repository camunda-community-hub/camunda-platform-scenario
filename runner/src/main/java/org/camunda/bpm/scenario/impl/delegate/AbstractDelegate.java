package org.camunda.bpm.scenario.impl.delegate;

/**
 * @author <a href="martin.schimak@plexiti.com">Martin Schimak</a>
 */
public abstract class AbstractDelegate<D> {

  protected D delegate;

  public AbstractDelegate(D delegate) {
    this.delegate = delegate;
  }

}
