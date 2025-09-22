package com.inorganic.workflows;

import static com.inorganic.IvrTemporalWorkerInitializer.IVR_NAMESPACE;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.Produces;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowClientOptions;
import io.temporal.serviceclient.WorkflowServiceStubs;
import io.temporal.serviceclient.WorkflowServiceStubsOptions;


@ApplicationScoped
public class TemporalClientProvider {
    
    private static final Logger LOG = Logger.getLogger(TemporalClientProvider.class);
    private final WorkflowClient workflowClient;

    @Inject
    public TemporalClientProvider(@ConfigProperty(name = "temporal.host") String host,
                                  @ConfigProperty(name = "temporal.port") int port) {
        LOG.info("[TemporalClientProvider] - TemporalClientProvider | Initializing Temporal client with host: " + host + ", port: " + port);
        WorkflowServiceStubs service = WorkflowServiceStubs.newServiceStubs(
                WorkflowServiceStubsOptions.newBuilder()
                        .setTarget(host + ":" + port)
                        .build());

        this.workflowClient = WorkflowClient.newInstance(service, WorkflowClientOptions.newBuilder().setNamespace(IVR_NAMESPACE).build());
        LOG.info("[TemporalClientProvider] - TemporalClientProvider | Workflow client successfully initialized for namespace: " + IVR_NAMESPACE);
    }

    @Produces
    @Named("temporal")
    public WorkflowClient getWorkflowClient() {
        return workflowClient;
    }
}
