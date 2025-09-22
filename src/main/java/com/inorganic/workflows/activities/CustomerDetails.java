package com.inorganic.workflows.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface CustomerDetails {

    @ActivityMethod
    String retrieveCustomerDetails(boolean hasGreeted, String customerId, String userName, String message, String conversationHistory);

    @ActivityMethod
    String retrieveCustomerName(String customerId);
}
