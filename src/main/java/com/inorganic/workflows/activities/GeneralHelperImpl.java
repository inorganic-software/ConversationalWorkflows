package com.inorganic.workflows.activities;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.inorganic.tools.exceptions.ToolException;

import io.temporal.failure.ApplicationFailure;

@ApplicationScoped
public class GeneralHelperImpl implements GeneralHelper{
    
    private static final Logger LOG = Logger.getLogger(GeneralHelperImpl.class);

    @Inject
    GeneralHelperPrompt promt;

    @Override
    public String getGeneraChatbotDescription(boolean hasGreeted, String userName, String message, String conversationHistory) {
        LOG.info("[GeneralHelperImpl] - getGeneraChatbotDescription | Processing general chatbot description for userName: " + userName + ", hasGreeted: " + hasGreeted);
        try {
            String response = promt.getGlobalDesc(userName, hasGreeted, message, conversationHistory);
            LOG.info("[GeneralHelperImpl] - getGeneraChatbotDescription | Successfully generated general chatbot description for userName: " + userName);
            return response;
        } catch (ToolException e) {
            LOG.error("[GeneralHelperImpl] - getGeneraChatbotDescription | ToolException occurred: " + e.getMessage());
            throw ApplicationFailure.newFailure(e.getMessage(), e.getErrorType());
        } catch (Exception e) {
            LOG.error("[GeneralHelperImpl] - getGeneraChatbotDescription | Error retrieving global description for userName: " + userName + ", error: " + e.getMessage());
            throw ApplicationFailure.newFailure("Error retrieving global description for userName: " + userName, "RetrieveGlobalDescFailure", e);
        }
    }
}
