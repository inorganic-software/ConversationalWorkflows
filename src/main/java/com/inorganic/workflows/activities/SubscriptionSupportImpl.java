package com.inorganic.workflows.activities;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.inorganic.tools.exceptions.ToolException;

import io.temporal.failure.ApplicationFailure;

@ApplicationScoped
public class SubscriptionSupportImpl implements SubscriptionSupport {
    
    private static final Logger LOG = Logger.getLogger(SubscriptionSupportImpl.class);

    @Inject
    SubscriptionSupportPrompt subscriptionSupportPrompt;

    @Override
    public String getSubscriptions(boolean hasGreeted, String request, String accountId, String conversationHistory) {
        LOG.info("[SubscriptionSupportImpl] - getSubscriptions | Processing subscriptions request for accountId: " + accountId);
        try {
            String subscriptions = subscriptionSupportPrompt.retrieveSubscriptions(accountId, hasGreeted, request, conversationHistory);
            LOG.info("[SubscriptionSupportImpl] - getSubscriptions | Successfully retrieved subscriptions for accountId: " + accountId);
            return subscriptions;
        } catch (ToolException e) {
            LOG.error("[SubscriptionSupportImpl] - getSubscriptions | ToolException occurred: " + e.getMessage());
            throw ApplicationFailure.newFailure(e.getMessage(), e.getErrorType());
        } catch (Exception e) {
            LOG.error("[SubscriptionSupportImpl] - getSubscriptions | Error retrieving subscriptions for accountId: " + accountId + ", error: " + e.getMessage());
            throw ApplicationFailure.newFailure("Error retrieving subscriptions for accountId: " + accountId + ". Cause: " + e.getMessage(), "GetSubscriptionsFailure");
        }
    }
}
