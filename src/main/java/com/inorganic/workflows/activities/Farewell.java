package com.inorganic.workflows.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface Farewell {
    @ActivityMethod
    String generateFarewell(boolean hasGreeted, String userName, String message, String conversationHistory);
}