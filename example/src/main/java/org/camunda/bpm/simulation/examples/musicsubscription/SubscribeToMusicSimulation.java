package org.camunda.bpm.simulation.examples.musicsubscription;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.simulation.Probability;
import org.camunda.bpm.simulation.ProcessSimulation;

import static org.camunda.bpm.engine.variable.Variables.createVariables;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class SubscribeToMusicSimulation {

    public static void main(String[] args) {

        // Set up the process engine defined in camunda.cfg.xml
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        // Create BPMN/DMN deployment
        processEngine.getRepositoryService().createDeployment()
            .addClasspathResource("org/camunda/bpm/simulation/examples/musicsubscription/SubscribeToMusic.bpmn")
            .addClasspathResource("org/camunda/bpm/simulation/examples/musicsubscription/DeterminePrepayment.dmn")
            .deploy();

        // Prepare simulation
        final ProcessSimulation simulation = new ProcessSimulation()

            // Defer finishing of all service tasks for which nothing special is defined
            .deferServiceTask().by(() -> "PT15S")

            // Defer finishing of all send tasks for which nothing special is defined
            .deferSendTask().by(() -> "PT5S")

            // Defer finishing of a specific user task by the period given as a function
            .deferUserTask("ProvideUpdatedPaymentData").by(() ->
                // Do not specify fix period, but select one of several values based on a given probability
                Probability.select("P1D", "P2D", "P3D", "P4D", "P5D", "P6D", "P7D", "P8D")
                    .withProbabilities(16, 14, 12, 10, 8, 6, 4, 30)
            )

            // Finish a specific Service Task with given map of variables
            .finishServiceTask("LoadCustomerData").with(() ->
                createVariables()
                    .putValue("postpaidCustomer",
                            Probability.select(true, false).withProbabilities(80, 20))
                    .putValue("customerValue",
                            Probability.select("A", "B", "C").withProbabilities(20, 60, 20))
                    .putValue("overdueReminder",
                            Probability.select(true, false).withProbabilities(25, 75))
                    .putValue("creditcardNumber",
                            Probability.select(null, "3456-7890-1234-5678").withProbabilities(35, 65))
            )

            // Finish a specific user task with given map of variables
            .finishUserTask("ProvideUpdatedPaymentData").with(() ->
                createVariables().putValue("creditcardNumber", Probability.select("3456-7890-1234-5678", null).withProbabilities(90, 10))
            )

            // Finish a specific service task with a given action
            .finishServiceTask("CollectPayment").with(task -> {
                String creditcardNumber = (String) processEngine.getRuntimeService().getVariable(task.getProcessInstanceId(), "creditcardNumber");
                if (creditcardNumber == null) {
                    task.handleBpmnError("PaymentCollectionFailed");
                } else {
                    task.complete();
                }
            });

        // Run the simulation a number of times
        simulation.startByKey("SubscribeToMusic").execute(1000);

    }

}
