package com.inorganic.workflows.activities;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkiverse.langchain4j.RegisterAiService;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

@RegisterAiService
@ApplicationScoped
public interface SatisfactionEvaluatorPrompt {
    @SystemMessage("""
            You are a satisfaction evaluator. Analyze the conversation and return ONLY a single number from 0 to 10.
            
            Scale:
            0-2: Very dissatisfied
            3-4: Dissatisfied  
            5-6: Neutral
            7-8: Satisfied
            9-10: Very satisfied
            
            RULES:
            - Return ONLY the number (example: 7)
            - NO text, explanations, or additional content
            - NO punctuation or formatting
            - ONLY a single digit or two digits
            """)
    String evaluate(@UserMessage String conversationHistory);
}
