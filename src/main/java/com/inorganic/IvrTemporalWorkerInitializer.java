package com.inorganic;

import java.io.IOException;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import com.google.protobuf.Duration;
import com.inorganic.workflows.IvrWorkflowImpl;
import com.inorganic.workflows.activities.AccountDetails;
import com.inorganic.workflows.activities.ConsentsSupport;
import com.inorganic.workflows.activities.ConversationSummary;
import com.inorganic.workflows.activities.CustomerDetails;
import com.inorganic.workflows.activities.Farewell;
import com.inorganic.workflows.activities.GeneralHelper;
import com.inorganic.workflows.activities.ProductSupport;
import com.inorganic.workflows.activities.RetrieveCustomerId;
import com.inorganic.workflows.activities.SatisfactionEvaluator;
import com.inorganic.workflows.activities.ScenarioDispatcher;
import com.inorganic.workflows.activities.SessionCleanup;
import com.inorganic.workflows.activities.StockSupport;
import com.inorganic.workflows.activities.SubscriptionSupport;

import io.quarkus.runtime.StartupEvent;
import io.temporal.api.workflowservice.v1.RegisterNamespaceRequest;
import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;
import io.temporal.worker.Worker;
import io.temporal.worker.WorkerFactory;

@ApplicationScoped
public class IvrTemporalWorkerInitializer {
    public static final String IVR_TASK_QUEUE = "IVR_WORKFLOW_TASK_QUEUE";
    public static final String IVR_NAMESPACE = "ivr";

    private static final Logger LOG = Logger.getLogger(IvrTemporalWorkerInitializer.class);
    private static final int THIRTY_DAYS = 2592000;

    @ConfigProperty(name = "temporal.host")
    String temporalHost;

    @ConfigProperty(name = "temporal.port")
    int temporalPort;

    @Inject
    RetrieveCustomerId retrieveCustomerId;

    @Inject
    AccountDetails accountDetails;

    @Inject
    ScenarioDispatcher scenarioDispatcher;

    @Inject
    ConsentsSupport consentsSupport;

    @Inject
    CustomerDetails customerDetails;

    @Inject
    ProductSupport productSupport;

    @Inject
    StockSupport stockSupport;

    @Inject
    SubscriptionSupport subscriptionSupport;

    @Inject
    GeneralHelper generalHelper;

    @Inject
    SessionCleanup sessionCleanup;

    @Inject
    ConversationSummary conversationSummary;

    @Inject
    SatisfactionEvaluator satisfactionEvaluator;

    @Inject
    Farewell farewell;

    private WorkerFactory workerFactory;

    public void onStart(@Observes StartupEvent ev) throws IOException {
        try {
            WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(WorkflowServiceStubsOptions.newBuilder().setTarget(temporalHost + ":" + temporalPort).build());

            try {
                service.blockingStub().registerNamespace(
                        RegisterNamespaceRequest.newBuilder()
                                .setNamespace(IVR_NAMESPACE)
                                .setWorkflowExecutionRetentionPeriod(Duration.newBuilder().setSeconds(THIRTY_DAYS).build())
                                .setDescription("IVR namespace for workflows")
                                .build()
                );

                LOG.info("[IvrTemporalWorkerInitializer] - onStart | Namespace 'ivr' registered.");
            } catch (Exception e) {
                LOG.warn("[IvrTemporalWorkerInitializer] - onStart | Namespace 'ivr' may already exist: " + e.getMessage());
            }

            WorkflowClient client = WorkflowClient.newInstance(
                    service,
                    WorkflowClientOptions.newBuilder().setNamespace(IVR_NAMESPACE).build()
            );

            workerFactory = WorkerFactory.newInstance(client);
            Worker worker = workerFactory.newWorker(IVR_TASK_QUEUE);
            worker.registerWorkflowImplementationTypes(IvrWorkflowImpl.class);
            worker.registerActivitiesImplementations(
                    retrieveCustomerId,
                    accountDetails,
                    subscriptionSupport,
                    stockSupport,
                    productSupport,
                    scenarioDispatcher,
                    consentsSupport,
                    customerDetails,
                    generalHelper,
                    sessionCleanup,
                    conversationSummary,
                    satisfactionEvaluator,
                    farewell);

            workerFactory.start();
            LOG.info("[IvrTemporalWorkerInitializer] - onStart | IVR Worker started on task queue: " + IVR_TASK_QUEUE);

        } catch (Exception e) {
            LOG.error("[IvrTemporalWorkerInitializer] - onStart | Failed to initialize IVR worker", e);
        }
    }

    public void onStop(@Observes io.quarkus.runtime.ShutdownEvent ev) {
        if (workerFactory != null) {
            try {
                workerFactory.shutdown();
                LOG.info("[IvrTemporalWorkerInitializer] - onStop | IVR WorkerFactory shut down.");
            } catch (Exception e) {
                LOG.warn("[IvrTemporalWorkerInitializer] - onStop | Failed to shut down WorkerFactory cleanly", e);
            }
        }
    }
}
