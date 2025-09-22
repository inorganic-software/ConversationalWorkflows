package com.inorganic.workflows.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface SessionCleanup {
    
    @ActivityMethod
    void cleanupUserSession(String userEmail);
}