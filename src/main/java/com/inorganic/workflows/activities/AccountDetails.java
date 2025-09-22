package com.inorganic.workflows.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface AccountDetails {

    @ActivityMethod
    String getAccountByCustomerId(String customerId);
}
