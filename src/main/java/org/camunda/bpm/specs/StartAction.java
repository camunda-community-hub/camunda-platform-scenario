package org.camunda.bpm.specs;

import org.camunda.bpm.engine.runtime.ProcessInstance;

public interface StartAction {

  ProcessInstance execute();

}
