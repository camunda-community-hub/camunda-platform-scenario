package org.camunda.bpm.specs.examples.jobannouncement;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.specs.Scenario;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.camunda.bpm.specs.ProcessEngineSpecs.*;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;
import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class JobAnnouncementProcessSpec {

  @Rule
  public final ProcessEngineRule processEngineRule = new ProcessEngineRule();

  @Mock
  public JobAnnouncementService jobAnnouncementService;
  @Mock
  public JobAnnouncement jobAnnouncement;

          // Some boilerplate - we can easily get rid of again by 
  @Before // deciding where to ultimately put the jUnit integration
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    Mocks.register("jobAnnouncementService", jobAnnouncementService);
    Mocks.register("jobAnnouncement", jobAnnouncement);
  }

  @After
  public void tearDown() {
    Mocks.reset();
  }
  
  @Test
  @Deployment(resources = {
    "camunda-testing-job-announcement.bpmn",
    "camunda-testing-job-announcement-publication.bpmn"
  })
  public void testHappyPath() {

    Scenario jobAnnouncementScenario = mock(Scenario.class);
    Scenario jobAnnouncementPublicationScenario = mock(Scenario.class);

    // given
    when(jobAnnouncementScenario.atTask("userTask1")).thenReturn((t, pi) ->
      execute(job("boundaryTimerEvent1", pi))
    );
    when(jobAnnouncementScenario.atTask("userTask2")).thenReturn((t, pi) ->
      complete(t, withVariables("bar", false))
    );
    when(jobAnnouncementScenario.atTask(anyString())).thenReturn((t, pi) ->
      complete(t)
    );
    when(jobAnnouncementScenario.atMessageEvent("messageEvent1")).thenReturn((e, pi) ->
      runtimeService().messageEventReceived(e.getEventName(), pi.getId())
    );
    when(jobAnnouncementScenario.atSignalEvent("signalEvent1")).thenReturn((e, pi) ->
      runtimeService().signalEventReceived(e.getEventName(), pi.getId())
    );
    when(jobAnnouncementScenario.atTimerEvent("timerEvent1")).thenReturn((j, pi) ->
      execute(j)
    );
    when(jobAnnouncementScenario.atEventBasedGateway("gateway1")).thenReturn((e, j, pi) ->
      runtimeService().messageEventReceived(e.get("messageEvent2").getEventName(), pi.getId())
    );
    when(jobAnnouncementPublicationScenario.atMessageEvent(anyString())).thenReturn((e, pi) ->
      runtimeService().correlateMessage(e.getEventName(), pi.getId())
    );
    when(jobAnnouncementScenario.startsCallActivity("callActivity1")).thenReturn(jobAnnouncementPublicationScenario);
    when(jobAnnouncementScenario.startsProcessInstance("jobAnnouncementPublication")).thenReturn(jobAnnouncementPublicationScenario);

    // when
    ProcessInstance jobAnnouncement = run(jobAnnouncementScenario)
      .fromStart(() -> runtimeService().startProcessInstanceByKey("testProcess"))
      .toEnd()
    .go();

    // when (alt 2)
    ProcessInstance jobAnnouncement2 = run(jobAnnouncementScenario)
      .fromStart()
      .toEnd()
    .go();

    // when (alt 3)
    ProcessInstance jobAnnouncement3 = run(jobAnnouncementScenario)
      .fromBefore("activity1", "activity2")
      .toBefore("activity3")
    .go();

    // then
    assertThat(jobAnnouncement).isEnded();
    assertThat(jobAnnouncement).hasPassed("theEnd1");
    assertThat(jobAnnouncement).hasNotPassed("theEnd2");
    assertThat(jobAnnouncement).hasPassedInOrder("userTask1", "userTask2");

    // and
    verify(jobAnnouncementScenario).atTask("userTask1");
    verify(jobAnnouncementScenario, times(1)).atTask("userTask2");
    verify(jobAnnouncementScenario, never()).atTask("userTask3");

  }

}
