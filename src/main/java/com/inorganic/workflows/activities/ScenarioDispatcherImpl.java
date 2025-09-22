package com.inorganic.workflows.activities;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.inorganic.tools.exceptions.ToolException;

import io.temporal.failure.ApplicationFailure;

@ApplicationScoped
public class ScenarioDispatcherImpl implements ScenarioDispatcher {
    
    private static final Logger LOG = Logger.getLogger(ScenarioDispatcherImpl.class);

    @Inject
    ScenarioDispatcherPrompt promt;

    @Override
    public String getScenario(String request, String conversationHistory) {
        LOG.info("[ScenarioDispatcherImpl] - getScenario | Determining scenario for request");
        try {
            String scenario = promt.getScenario(request, conversationHistory);
            LOG.info("[ScenarioDispatcherImpl] - getScenario | Scenario determined: " + scenario);
            return scenario;
        } catch (ToolException e) {
            LOG.error("[ScenarioDispatcherImpl] - getScenario | ToolException occurred: " + e.getMessage());
            throw ApplicationFailure.newFailure(e.getMessage(), e.getErrorType());
        } catch (Exception e) {
            LOG.error("[ScenarioDispatcherImpl] - getScenario | Error retrieving scenario for request, error: " + e.getMessage());
            throw ApplicationFailure.newFailure("Error retrieving scenario for request: " + request + ". Cause: " + e.getMessage(), "GetScenarioFailure");
        }
    }
}
