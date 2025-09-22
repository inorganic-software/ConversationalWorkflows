package com.inorganic.workflows.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface SatisfactionEvaluator {
    
    @ActivityMethod
    int evaluateSatisfaction(String conversationHistory);
}