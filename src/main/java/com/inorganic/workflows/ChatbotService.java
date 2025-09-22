package com.inorganic.workflows;

import static com.inorganic.IvrTemporalWorkerInitializer.IVR_TASK_QUEUE;

import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.logging.Logger;

import io.temporal.client.WorkflowClient;
import io.temporal.client.WorkflowExecutionAlreadyStarted;
import io.temporal.client.WorkflowOptions;

@ApplicationScoped
public class ChatbotService {

    private static final Logger LOG = Logger.getLogger(ChatbotService.class);
    private static final int MAX_WAIT_ATTEMPTS = 300; // 5 minutes timeout (300 * 1000ms)
    private static final int POLLING_INTERVAL_MS = 1000; // 1 second
    private static final String TIMEOUT_ERROR_MESSAGE = "Timeout waiting for response";
    private static final String PROCESSING_ERROR_MESSAGE = "Error processing message";
    
    @Inject
    @Named("temporal")
    WorkflowClient workflowClient;
    
    public String processMessage(String userEmail, String message) {
        LOG.info("[ChatbotService] - processMessage | Processing message for user: " + userEmail);
        IvrWorkflow workflow = getOrCreateWorkflowForUser(userEmail);
        String requestId = generateRequestId();
        workflow.processMessage(message, requestId);
        return waitForResponse(workflow, userEmail, requestId);
    }

    private String generateRequestId() {
        return UUID.randomUUID().toString();
    }
    
    private String waitForResponse(IvrWorkflow workflow, String userEmail, String requestId) {
        try {
            for (int attempt = 0; attempt < MAX_WAIT_ATTEMPTS; attempt++) {
                if (workflow.isResponseReady(requestId)) {
                    String response = workflow.getResponse(requestId);
                    LOG.info("[ChatbotService] - waitForResponse | Response ready for user: " + userEmail + " after " + attempt + " attempts");
                    return response;
                }
                Thread.sleep(POLLING_INTERVAL_MS);
            }
            
            LOG.error("[ChatbotService] - waitForResponse | Timeout waiting for response for user: " + userEmail + ", requestId: " + requestId);
            return TIMEOUT_ERROR_MESSAGE;
            
        } catch (InterruptedException e) {
            LOG.error("[ChatbotService] - waitForResponse | Thread interrupted while processing message for user: " + userEmail);
            Thread.currentThread().interrupt();
            return PROCESSING_ERROR_MESSAGE;
        }
    }
    
    private IvrWorkflow getOrCreateWorkflowForUser(String userEmail) {
        LOG.debug("[ChatbotService] - getOrCreateWorkflowForUser | Getting workflow for user: " + userEmail);
        String workflowId = "ivr-session-" + userEmail.replaceAll("[^a-zA-Z0-9]", "-");
        LOG.info("[ChatbotService] - getOrCreateWorkflowForUser | Using workflowId: " + workflowId + " for user: " + userEmail);

        WorkflowOptions options = WorkflowOptions.newBuilder()
                .setTaskQueue(IVR_TASK_QUEUE)
                .setWorkflowId(workflowId)
                .build();

        IvrWorkflow workflow = workflowClient.newWorkflowStub(IvrWorkflow.class, options);

        try {
            WorkflowClient.start(workflow::startSession, userEmail);
            LOG.info("[ChatbotService] - getOrCreateWorkflowForUser | New workflow started successfully for user: " + userEmail + ", workflowId: " + workflowId);
        } catch (WorkflowExecutionAlreadyStarted e) {
            LOG.info("[ChatbotService] - getOrCreateWorkflowForUser | Workflow already exists for user: " + userEmail + ", connecting to existing workflow, workflowId: " + workflowId);
        } catch (Exception e) {
            String errorMessage = e.getMessage();
            if (errorMessage != null && (errorMessage.contains("ALREADY_EXISTS") || errorMessage.contains("already running"))) {
                LOG.info("[ChatbotService] - getOrCreateWorkflowForUser | Workflow already running for user: " + userEmail + ", connecting to existing workflow. Error type: " + e.getClass().getSimpleName() + ", workflowId: " + workflowId);
            } else {
                LOG.error("[ChatbotService] - getOrCreateWorkflowForUser | Failed to start workflow for user: " + userEmail + ". Error type: " + e.getClass().getSimpleName() + ", workflowId: " + workflowId, e);
                throw new RuntimeException("Failed to create or connect to workflow for user: " + userEmail, e);
            }
        }

        return workflow;
    }
    
}