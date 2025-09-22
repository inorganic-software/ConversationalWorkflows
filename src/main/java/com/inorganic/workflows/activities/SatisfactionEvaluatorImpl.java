package com.inorganic.workflows.activities;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

@ApplicationScoped
public class SatisfactionEvaluatorImpl implements SatisfactionEvaluator {
    
    private static final Logger LOG = Logger.getLogger(SatisfactionEvaluatorImpl.class);
    
    @Inject
    SatisfactionEvaluatorPrompt satisfactionEvaluatorPrompt;
    
    @Override
    public int evaluateSatisfaction(String conversationHistory) {
        LOG.info("[SatisfactionEvaluatorImpl] - evaluateSatisfaction | Starting satisfaction evaluation for conversation");
        try {
            String result = satisfactionEvaluatorPrompt.evaluate(conversationHistory);
            int satisfaction = Integer.parseInt(result.trim());
            LOG.info("[SatisfactionEvaluatorImpl] - evaluateSatisfaction | Satisfaction score evaluated: " + satisfaction);
            return satisfaction;
        } catch (Exception e) {
            LOG.error("[SatisfactionEvaluatorImpl] - evaluateSatisfaction | Error evaluating satisfaction: " + e.getMessage(), e);
            LOG.warn("[SatisfactionEvaluatorImpl] - evaluateSatisfaction | Returning default satisfaction score: 5");
            return 5;
        }
    }
}