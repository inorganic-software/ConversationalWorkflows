package com.inorganic.workflows.activities;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.inorganic.tools.exceptions.ToolException;

import io.temporal.failure.ApplicationFailure;

@ApplicationScoped
public class ConsentsSupportImpl implements ConsentsSupport {
    
    private static final Logger LOG = Logger.getLogger(ConsentsSupportImpl.class);

    @Inject
    ConsentsSupportPrompt promt;

    @Override
    public String retrieveConsents(boolean hasGreeted, String userId, String userName, String message, String conversationHistory) {
        LOG.info("[ConsentsSupportImpl] - retrieveConsents | Processing consents request for userId: " + userId + ", userName: " + userName + ", hasGreeted: " + hasGreeted);
        try {
            String response = promt.retrieveConsents(userId, userName, hasGreeted, message, conversationHistory);
            LOG.info("[ConsentsSupportImpl] - retrieveConsents | Successfully retrieved consents response for userId: " + userId);
            return response;
        } catch (ToolException e) {
            LOG.error("[ConsentsSupportImpl] - retrieveConsents | ToolException occurred: " + e.getMessage());
            throw ApplicationFailure.newFailure(e.getMessage(), e.getErrorType());
        } catch (Exception e) {
            LOG.error("[ConsentsSupportImpl] - retrieveConsents | Error retrieving consents for userId: " + userId + ", userName: " + userName + ", error: " + e.getMessage());
            throw ApplicationFailure.newFailure("Error retrieving consents for userId: " + userId + ", userName: " + userName, "RetrieveConsentsFailure", e);
        }
    }
}
