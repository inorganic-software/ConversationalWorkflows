package com.inorganic.workflows.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface SubscriptionSupport {

    @ActivityMethod
    String getSubscriptions(boolean hasGreeted, String request, String accountId, String conversationHistory);
}
