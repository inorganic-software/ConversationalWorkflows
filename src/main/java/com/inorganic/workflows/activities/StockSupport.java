package com.inorganic.workflows.activities;

import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface StockSupport {
    @ActivityMethod
    String getStock(boolean hasGreeted, String request, String productId, String conversationHistory);
}
