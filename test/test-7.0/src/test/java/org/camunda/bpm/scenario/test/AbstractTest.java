package org.camunda.bpm.scenario.test;

import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.scenario.ProcessScenario;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;

/**
 * @author Martin Schimak
 */
public class AbstractTest {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule();
  public Map<String, Object> variables = new HashMap<String, Object>();
  @Mock
  protected ProcessScenario scenario;
  @Mock
  protected ProcessScenario otherScenario;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    Mocks.register("javaDelegate", mock(JavaDelegate.class));
  }

}
