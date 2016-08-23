package org.camunda.bpm.scenario.test;

import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.scenario.Scenario;
import org.junit.Before;
import org.junit.Rule;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class AbstractTest {

  @Rule
  public ProcessEngineRule rule = new ProcessEngineRule();

  @Mock
  protected Scenario.Process scenario;

  @Mock
  protected Scenario.Process otherScenario;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

}
