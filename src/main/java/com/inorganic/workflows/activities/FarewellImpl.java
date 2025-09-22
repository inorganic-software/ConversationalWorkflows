package com.inorganic.workflows.activities;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import io.temporal.failure.ApplicationFailure;

@ApplicationScoped
public class FarewellImpl implements Farewell {
    
    private static final Logger LOG = Logger.getLogger(FarewellImpl.class);
    
    @Inject
    FarewellPrompt farewellPrompt;
    
    @Override
    public String generateFarewell(boolean hasGreeted, String userName, String message, String conversationHistory) {
        LOG.info("[FarewellImpl] - generateFarewell | Generating farewell for user: " + userName);
        try {
            String farewell = farewellPrompt.generateFarewell(userName, hasGreeted, message, conversationHistory);
            LOG.info("[FarewellImpl] - generateFarewell | Successfully generated farewell for user: " + userName);
            return farewell + " SESSION_END";
        } catch (Exception e) {
            LOG.error("[FarewellImpl] - generateFarewell | Error generating farewell for user: " + userName + ", error: " + e.getMessage(), e);
            throw ApplicationFailure.newFailure("Error generating farewell for user: " + userName, "GenerateFarewellFailure", e);
        }
    }
}