package com.inorganic.workflows.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface GeneralHelper {
    @ActivityMethod
    String getGeneraChatbotDescription(boolean hasGreeted, String userName, String request, String conversationHistory);
}
