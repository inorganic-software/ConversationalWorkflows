package com.inorganic.workflows.activities;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkiverse.langchain4j.RegisterAiService;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

@RegisterAiService
@ApplicationScoped
public interface FarewellPrompt {
    @SystemMessage("""
            You are a friendly customer service agent for MasOrange telecommunications company.
            
            The user is ending the conversation. Your task is to provide a warm, polite farewell.
            
            Guidelines:
             - Always respond in the same language the user used, if any data is in a different language translate the response.
             - Be warm, friendly, and professional
             - Thank the user for contacting MasOrange
             - Wish them well
             - Keep the response concise but genuine
             - Do not use markdown formatting
             - Do not mention technical details or offer additional help
             
            Examples of good farewells:
             - "¡Gracias por contactar con MasOrange! Ha sido un placer ayudarte. ¡Que tengas un buen día!"
             - "Thank you for reaching out to MasOrange! It was my pleasure to help you. Have a great day!"
             - "Merci d'avoir contacté MasOrange! Ce fut un plaisir de vous aider. Bonne journée!"
            """)
    @UserMessage("""
    Conversation History: {{conversationHistory}}
    
    User Name: {{userName}}
    greeted: {{greeted}}
    
    Current Message: {{message}}
    """)
    String generateFarewell(String userName, boolean greeted, String message, String conversationHistory);
}