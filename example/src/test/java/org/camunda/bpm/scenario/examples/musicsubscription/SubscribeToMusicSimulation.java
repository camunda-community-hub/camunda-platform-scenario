package org.camunda.bpm.scenario.examples.musicsubscription;

import org.camunda.bpm.engine.impl.test.ProcessEngineAssert;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.assertions.ProcessEngineAssertions;
import org.camunda.bpm.extension.process_test_coverage.junit.rules.TestCoverageProcessEngineRuleBuilder;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.simulation.Probability;
import org.camunda.bpm.scenario.simulation.ProcessSimulation;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.camunda.bpm.engine.variable.Variables.createVariables;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
@Deployment(resources = {
        "org/camunda/bpm/scenario/examples/musicsubscription/SubscribeToMusic.bpmn",
        "org/camunda/bpm/scenario/examples/musicsubscription/DeterminePrepayment.dmn"
})
public class SubscribeToMusicSimulation {

    @Rule @ClassRule
    public static ProcessEngineRule rule = TestCoverageProcessEngineRuleBuilder.create().withDetailedCoverageLogging().build();

    @Mock
    private ProcessSimulation simulation;

    @Before
    public void setup() {

        rule.getRepositoryService().createDeployment()
                .addClasspathResource("org/camunda/bpm/scenario/examples/musicsubscription/SubscribeToMusic.bpmn")
                .addClasspathResource("org/camunda/bpm/scenario/examples/musicsubscription/DeterminePrepayment.dmn")
                .deploy();

        MockitoAnnotations.initMocks(this);

        when(simulation.decidesAboutDeferringServiceTask(any())).thenReturn(task -> "PT15S");

        when(simulation.finishesServiceTask("LoadCustomerData")).thenReturn(task ->
                createVariables()
                        .putValue("postpaidCustomer", new Probability(50, 50).values(true, false))
                        .putValue("customerValue", new Probability(20, 60, 20).values("A", "B", "C"))
                        .putValue("overdueReminder", new Probability(25, 75).values(true, false))
                        .putValue("creditcardNumber", new Probability(35, 65).values(null, "3456-7890-1234-5678"))
        );

        when(simulation.decidesAboutDeferringUserTask("ProvideUpdatedPaymentData")).thenReturn(task ->
                new Probability(16, 14, 12, 10, 8, 6, 4, 30).values("P1D", "P2D", "P3D", "P4D", "P5D", "P6D", "P7D", "P8D")
        );

        when(simulation.finishesUserTask("ProvideUpdatedPaymentData")).thenReturn(task ->
                createVariables().putValue("creditcardNumber", new Probability(90, 10).values("3456-7890-1234-5678", null))
        );

        when(simulation.decidesAboutDeferringSendTask(any())).thenReturn(task -> "PT5S");

        when(simulation.waitsAtServiceTask(any())).thenCallRealMethod();
        when(simulation.waitsAtSendTask(any())).thenCallRealMethod();
        when(simulation.waitsAtUserTask(any())).thenCallRealMethod();

        when(simulation.waitsAtServiceTask("CollectPayment")).thenReturn(task -> {
            String creditcardNumber = (String) rule.getRuntimeService().getVariable(task.getProcessInstanceId(), "creditcardNumber");
            if (creditcardNumber == null) {
                task.handleBpmnError("PaymentCollectionFailed");
            } else {
                task.complete();
            }
        });

    }

    @Test
    public void simulate() throws Exception {

        for (int i = 0; i < 1000; i++) {
            Scenario scenario = Scenario.run(simulation).startByKey("SubscribeToMusic").execute();
            ProcessEngineAssertions.assertThat(scenario.instance(simulation)).isEnded();
        }

        Thread.sleep(5000);

    }

}
