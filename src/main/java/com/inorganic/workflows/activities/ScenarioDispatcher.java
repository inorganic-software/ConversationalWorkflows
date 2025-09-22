package com.inorganic.workflows.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface ScenarioDispatcher {

    @ActivityMethod
    String getScenario(String request, String conversationHistory);
}
