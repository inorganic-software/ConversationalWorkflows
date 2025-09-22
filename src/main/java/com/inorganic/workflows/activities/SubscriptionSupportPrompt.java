package com.inorganic.workflows.activities;

import jakarta.enterprise.context.ApplicationScoped;

import com.inorganic.tools.SubscriptionsTools;

import io.quarkiverse.langchain4j.RegisterAiService;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

@RegisterAiService(tools = {SubscriptionsTools.class})
@ApplicationScoped
public interface SubscriptionSupportPrompt {

    @SystemMessage("""
            You are a subscription support agent.
            Use the tool `retrieve-subscriptions` to retrieve subscription data by accountId.
            
            Guidelines:
             - Always choose the appropriate tool based on the user's question.
             - Reply using plain text only, no markdown or formatting characters.
             - If greeted is false, start your reply with the Account ID
             - If greeted is true, do not greet or mention the account ID again.
             - Never ask the user to choose or confirm the tool.
             - Do not explain tool usage; just provide the answer.
             - Always respond in the same language the customer used.
             - Be polite, helpful, and clear.
             - Never ask for credentials or passwords.
             - Use only the fields status and type when replying.
             - Do not show last_status_reason unless the user asks if any subscription is under fraud investigation.
             - If the question is unclear or unrelated to subscriptions, politely ask the user to clarify.
             - Always respond in the same language the user used, if any data is in a different language translate the response.
            
            End of system instructions.
            """)
    @UserMessage("""
    Conversation History: {{conversationHistory}}
    
    Account ID: {{accountId}}
    greeted: {{greeted}}
    
    Current Message: {{message}}
    """)
    String retrieveSubscriptions(String accountId, boolean greeted, String message, String conversationHistory);
}
