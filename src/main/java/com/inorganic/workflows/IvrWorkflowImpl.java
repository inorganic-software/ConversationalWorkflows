package com.inorganic.workflows;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

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

import io.temporal.common.RetryOptions;
import io.temporal.failure.ApplicationFailure;
import io.temporal.workflow.Workflow;

public class IvrWorkflowImpl implements IvrWorkflow {
    private static final int USER_INACTIVITY_TIMEOUT_MINUTES = 5;
    private static final String USER_CONVERSATION_PREFIX = "User: ";
    private static final String ASSISTANT_CONVERSATION_PREFIX = "Assistant: ";
    private static final String CONVERSATION_DELIMITER = "\n";
    private static final String PRODUCT_NOT_FOUND_RESPONSE = "No product found.";
    private static final String UNEXPECTED_ERROR_RESPONSE = "Unexpected error occurred. Please contact support.";
    private static final String SESSION_END_MARKER = "SESSION_END";
    
    private boolean sessionEndRequested = false;
    
    private boolean greeted = false;
    private String userEmail;
    private String customerId;
    private String accountId;
    private String userName;
    
    private final List<MessageRequest> pendingRequests = new ArrayList<>();
    private final ConcurrentHashMap<String, String> responses = new ConcurrentHashMap<>();
    
    private final List<String> conversationHistory = new ArrayList<>();
    private String conversationSummaryText = "";
    private int satisfactionScore = 5;
    
    private static class MessageRequest {
        final String message;
        final String requestId;
        
        MessageRequest(String message, String requestId) {
            this.message = message;
            this.requestId = requestId;
        }
    }
    
    RetryOptions retryOptions = RetryOptions.newBuilder()
            .setInitialInterval(Duration.ofSeconds(5))
            .setMaximumInterval(Duration.ofSeconds(30))
            .setBackoffCoefficient(2.0)
            .setMaximumAttempts(5)
            .build();

    private final RetrieveCustomerId retrieveCustomerId = Workflow.newActivityStub(
            RetrieveCustomerId.class,
            io.temporal.activity.ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(60))
                    .setRetryOptions(retryOptions)
                    .build()
    );

    private final AccountDetails retrieveAccountId = Workflow.newActivityStub(
            AccountDetails.class,
            io.temporal.activity.ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(60))
                    .setRetryOptions(retryOptions)
                    .build()
    );

    private final ProductSupport productSupport = Workflow.newActivityStub(
            ProductSupport.class,
            io.temporal.activity.ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(60))
                    .setRetryOptions(retryOptions)
                    .build()
    );

    private final StockSupport stockSupport = Workflow.newActivityStub(
            StockSupport.class,
            io.temporal.activity.ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(60))
                    .setRetryOptions(retryOptions)
                    .build()
    );

    private final GeneralHelper generalHelper = Workflow.newActivityStub(
            GeneralHelper.class,
            io.temporal.activity.ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(60))
                    .setRetryOptions(retryOptions)
                    .build()
    );

    private final ScenarioDispatcher scenarioDispatcher = Workflow.newActivityStub(
            ScenarioDispatcher.class,
            io.temporal.activity.ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(60))
                    .setRetryOptions(retryOptions)
                    .build()
    );

    private final ConsentsSupport consentsSupport = Workflow.newActivityStub(
            ConsentsSupport.class,
            io.temporal.activity.ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(60))
                    .setRetryOptions(retryOptions)
                    .build()
    );

    private final CustomerDetails customerDetails = Workflow.newActivityStub(
            CustomerDetails.class,
            io.temporal.activity.ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(60))
                    .setRetryOptions(retryOptions)
                    .build()
    );

    private final SubscriptionSupport subscriptionSupport = Workflow.newActivityStub(
            SubscriptionSupport.class,
            io.temporal.activity.ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(60))
                    .setRetryOptions(retryOptions)
                    .build()
    );

    private final SessionCleanup sessionCleanup = Workflow.newActivityStub(
            SessionCleanup.class,
            io.temporal.activity.ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(30))
                    .setRetryOptions(retryOptions)
                    .build()
    );

    private final ConversationSummary conversationSummary = Workflow.newActivityStub(
            ConversationSummary.class,
            io.temporal.activity.ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(60))
                    .setRetryOptions(retryOptions)
                    .build()
    );

    private final SatisfactionEvaluator satisfactionEvaluator = Workflow.newActivityStub(
            SatisfactionEvaluator.class,
            io.temporal.activity.ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(60))
                    .setRetryOptions(retryOptions)
                    .build()
    );

    private final Farewell farewell = Workflow.newActivityStub(
            Farewell.class,
            io.temporal.activity.ActivityOptions.newBuilder()
                    .setStartToCloseTimeout(Duration.ofSeconds(60))
                    .setRetryOptions(retryOptions)
                    .build()
    );

    @Override
    public void startSession(String userEmail) {
        this.userEmail = userEmail;
        var logger = Workflow.getLogger(IvrWorkflowImpl.class);
        logger.info("[IvrWorkflowImpl] - startSession | Starting session for user: " + userEmail);
        
        initializeUserData(userEmail, logger);
        runSessionLoop(userEmail, logger);
    }
    
    private void initializeUserData(String userEmail, org.slf4j.Logger logger) {
        try {
            this.customerId = retrieveCustomerId.getCustomerId(userEmail);
            this.accountId = retrieveAccountId.getAccountByCustomerId(customerId);
            this.userName = customerDetails.retrieveCustomerName(customerId);
            logger.info("[IvrWorkflowImpl] - initializeUserData | Successfully initialized user data - customerId: " + customerId + ", accountId: " + accountId + ", userName: " + userName);
        } catch (Exception e) {
            logger.warn("[IvrWorkflowImpl] - initializeUserData | Failed to initialize user data for " + userEmail, e);
        }
    }
    
    private void runSessionLoop(String userEmail, org.slf4j.Logger logger) {
        while (!sessionEndRequested) {
            boolean hasMessage = Workflow.await(
                Duration.ofMinutes(USER_INACTIVITY_TIMEOUT_MINUTES),
                () -> !pendingRequests.isEmpty() || sessionEndRequested
            );

            if (!hasMessage) {
                logger.info("[IvrWorkflowImpl] - runSessionLoop | Session timeout for user: " + userEmail);
                handleSessionTimeout(userEmail, logger);
                break;
            }

            if (sessionEndRequested) {
                logger.info("[IvrWorkflowImpl] - runSessionLoop | Session end requested by user: " + userEmail);
                handleSessionTimeout(userEmail, logger);
                break;
            }

            processPendingRequests(logger);
        }
    }
    
    private void handleSessionTimeout(String userEmail, org.slf4j.Logger logger) {
        logger.info("[IvrWorkflowImpl] - handleSessionTimeout | Session timeout for user: " + userEmail);
        
        if (!conversationHistory.isEmpty()) {
            generateSessionSummary(logger);
        }
        
        sessionCleanup.cleanupUserSession(userEmail);
    }
    
    private void generateSessionSummary(org.slf4j.Logger logger) {
        String historyText = String.join(CONVERSATION_DELIMITER, conversationHistory);
        logger.info("[IvrWorkflowImpl] - generateSessionSummary | Conversation history size: " + conversationHistory.size() + " entries");
        logger.info("[IvrWorkflowImpl] - generateSessionSummary | Full history being summarized: " + historyText);
        this.conversationSummaryText = conversationSummary.summarizeConversation(historyText);
        this.satisfactionScore = satisfactionEvaluator.evaluateSatisfaction(historyText);
        logger.info("[IvrWorkflowImpl] - generateSessionSummary | Satisfaction score: " + satisfactionScore);
    }
    
    private void processPendingRequests(org.slf4j.Logger logger) {
        while (!pendingRequests.isEmpty()) {
            MessageRequest request = pendingRequests.remove(0);
            String response = processMessageInternal(request.message);

            if (response != null && response.contains(SESSION_END_MARKER)) {
                logger.info("[IvrWorkflowImpl] - processPendingRequests | Session end detected in response");
                response = response.replace(SESSION_END_MARKER, "").trim();
                sessionEndRequested = true;
            }

            responses.put(request.requestId, response);
            addToConversationHistory(request.message, response);
            logger.info("[IvrWorkflowImpl] - processPendingRequests | RESPONSE STORED - requestId: " + request.requestId + ", responses map size: " + responses.size());
        }
    }
    
    private void addToConversationHistory(String userMessage, String assistantResponse) {
        conversationHistory.add(USER_CONVERSATION_PREFIX + userMessage);
        conversationHistory.add(ASSISTANT_CONVERSATION_PREFIX + assistantResponse);
    }

    @Override
    public void processMessage(String message, String requestId) {
        var logger = Workflow.getLogger(IvrWorkflowImpl.class);
        logger.info("[IvrWorkflowImpl] - processMessage | Received message for requestId: " + requestId);
        pendingRequests.add(new MessageRequest(message, requestId));
    }
    
    @Override
    public String getResponse(String requestId) {
        String response = responses.get(requestId);
        Workflow.getLogger(IvrWorkflowImpl.class).debug("[IvrWorkflowImpl] - getResponse | Getting response for requestId: " + requestId + ", found: " + (response != null));
        return response;
    }
    
    @Override
    public boolean isResponseReady(String requestId) {
        boolean ready = responses.containsKey(requestId);
        Workflow.getLogger(IvrWorkflowImpl.class).debug("[IvrWorkflowImpl] - isResponseReady | Checking response for requestId: " + requestId + ", ready: " + ready);
        return ready;
    }
    
    @Override
    public String getConversationSummary() {
        return conversationSummaryText;
    }
    
    @Override
    public int getSatisfactionScore() {
        return satisfactionScore;
    }
    
    private String getFormattedConversationHistory() {
        if (conversationHistory.isEmpty()) {
            return "";
        }
        return String.join("\n", conversationHistory);
    }
    
    private String processMessageInternal(String message) {
        Workflow.getLogger(IvrWorkflowImpl.class).info("[IvrWorkflowImpl] - processMessageInternal | Processing message: " + message);
        try {
            String history = getFormattedConversationHistory();
            String scenarioResp = scenarioDispatcher.getScenario(message, history);
            String cleanedScenario = scenarioResp.replaceAll("^\"|\"$", "").toUpperCase();
            Workflow.getLogger(IvrWorkflowImpl.class).debug("[IvrWorkflowImpl] - processMessageInternal | Scenario determined: " + cleanedScenario);
            switch (cleanedScenario) {
                case "CONSENTS" -> {
                    return consentsSupport.retrieveConsents(greeted, customerId, userName, message, history);
                }
                case "USER_DETAILS" -> {
                    return customerDetails.retrieveCustomerDetails(greeted, customerId, userName, message, history);
                }
                case "PRODUCT" -> {
                    return productSupport.retrieveProducts(greeted, customerId, userName, message, history);
                }
                case "STOCK" -> {
                    String productId = productSupport.retrieveProductId(customerId, userName, message, history);
                    if ("NOT_FOUND".equals(productId)) {
                        return PRODUCT_NOT_FOUND_RESPONSE;
                    }
                    return stockSupport.getStock(greeted, message, productId, history);
                }
                case "SUBSCRIPTIONS" -> {
                    return subscriptionSupport.getSubscriptions(greeted, message, accountId, history);
                }
                case "FAREWELL" -> {
                    String farewellResponse = farewell.generateFarewell(greeted, userName, message, history);
                    generateSessionSummary(Workflow.getLogger(IvrWorkflowImpl.class));
                    return farewellResponse;
                }
                default -> {
                    return generalHelper.getGeneraChatbotDescription(greeted, userName, message, history);
                }
            }
        } catch (ApplicationFailure e) {
            return e.getMessage();
        } catch (Exception e) {
            return UNEXPECTED_ERROR_RESPONSE;
        } finally {
            if (!greeted) {
                greeted = true;
            }
        }
    }
}
