package com.inorganic.workflows;

import io.temporal.workflow.QueryMethod;
import io.temporal.workflow.SignalMethod;
import io.temporal.workflow.WorkflowInterface;
import io.temporal.workflow.WorkflowMethod;

@WorkflowInterface
public interface IvrWorkflow {

    @WorkflowMethod
    void startSession(String userEmail);
    
    @SignalMethod
    void processMessage(String message, String requestId);
    
    @QueryMethod
    String getResponse(String requestId);
    
    @QueryMethod
    boolean isResponseReady(String requestId);
    
    @QueryMethod
    String getConversationSummary();
    
    @QueryMethod
    int getSatisfactionScore();
}
