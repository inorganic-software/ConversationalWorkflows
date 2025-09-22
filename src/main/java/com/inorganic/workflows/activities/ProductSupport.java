package com.inorganic.workflows.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface ProductSupport {

    @ActivityMethod
    String retrieveProducts(boolean hasGreeted, String userId, String userName, String message, String conversationHistory);

    @ActivityMethod
    String retrieveProductId(String userId, String userName, String message, String conversationHistory);
}
