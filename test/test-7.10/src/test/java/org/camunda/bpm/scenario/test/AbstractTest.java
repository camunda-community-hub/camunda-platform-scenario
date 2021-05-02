package org.camunda.bpm.scenario.test;

import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.scenario.ProcessScenario;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author <a href="martin.schimak@plexiti.com">Martin Schimak</a>
 */
public class AbstractTest {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule();

  @Mock
  protected ProcessScenario scenario;

  @Mock
  protected ProcessScenario otherScenario;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

}
