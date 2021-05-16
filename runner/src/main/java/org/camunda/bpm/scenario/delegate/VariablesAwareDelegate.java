package org.camunda.bpm.scenario.delegate;

import java.util.Map;

public interface VariablesAwareDelegate {

  /**
   * Get the instance variables this object is associated to.
   *
   * @return instance variables this object is associated to
   */
  Map<String, Object> getVariables();

}
