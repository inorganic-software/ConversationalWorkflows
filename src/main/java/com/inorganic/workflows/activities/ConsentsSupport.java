package com.inorganic.workflows.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface ConsentsSupport {

    @ActivityMethod
    String retrieveConsents(boolean hasGreeted, String userId, String userName, String message, String conversationHistory);

}
