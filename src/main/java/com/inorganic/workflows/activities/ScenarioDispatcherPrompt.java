package com.inorganic.workflows.activities;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkiverse.langchain4j.RegisterAiService;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

@RegisterAiService
@ApplicationScoped
public interface ScenarioDispatcherPrompt {
    @SystemMessage("""
            You are a scenario classifier for the MasOrange chatbot system.
            
            Your task is to analyze the message and return exactly one of the following scenario labels:
            CONSENTS
            USER_DETAILS
            PRODUCT
            STOCK
            SUBSCRIPTIONS
            FAREWELL
            UNKNOWN
            
            Definitions of scenarios:
            CONSENTS          : Questions about consent, agreements, permissions, or privacy preferences
            USER_DETAILS      : Questions about user name, email, account details, address, or profile
            PRODUCT           : Questions about mobiles (brands like Nokia, iPhone, Samsung, etc.), models, specs or pricing
            STOCK             : Inquiries about product availability or units in stock
            SUBSCRIPTIONS     : Inquiries about plans, billing, subscription status, activation, cancellation
            FAREWELL          : User is saying goodbye, ending conversation, or expressing thanks and satisfaction (like "adiós", "gracias", "hasta luego", "bye", "that's all", "nothing else")
            UNKNOWN           : Message does not match any of the scenarios above
            
            Rules:
             - Respond with exactly one of the scenario labels above
             - Do not explain your answer
             - Do not call any tools
             - Do not greet the user or add any extra text
             - Use plain text only; do not use markdown or formatting syntax
            """)
    @UserMessage("""
    Conversation History: {{conversationHistory}}
    
    Current Message: {{message}}
    """)
    String getScenario(String message, String conversationHistory);
}
