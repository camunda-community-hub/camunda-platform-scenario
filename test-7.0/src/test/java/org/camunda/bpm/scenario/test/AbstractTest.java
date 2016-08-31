package org.camunda.bpm.scenario.test;

import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.scenario.ProcessScenario;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class AbstractTest {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule();

  @Mock
  protected ProcessScenario scenario;

  @Mock
  protected ProcessScenario otherScenario;

  public Map<String, Object> variables = new HashMap<String, Object>();

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

}
