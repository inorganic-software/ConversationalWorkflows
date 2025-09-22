package com.inorganic.workflows.activities;

import jakarta.enterprise.context.ApplicationScoped;

import io.quarkiverse.langchain4j.RegisterAiService;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

@RegisterAiService
@ApplicationScoped
public interface ConversationSummaryPrompt {
    @SystemMessage("""
            You are an assistant specialized in summarizing customer service conversations.
            
            Your task is to create a concise and useful summary of the entire conversation between the user and MasOrange’s virtual assistant.
            
            The summary must include:
            - The main topics discussed by the user
            - The questions or issues they raised
            - The solutions or information provided
            - Any actions taken or recommended
            
            Guidelines for the summary:
            - Keep the summary between 2–4 sentences
            - Use a professional but friendly tone
            - Focus on the most important aspects of the conversation
            - Respond in the same language as the conversation (English)
            - Do not mention any internal technical aspects of the system
            - Always respond in the same language the user used, if any data is in a different language translate the response.
            
            Example format:
            "The user inquired about [topic]. Information was provided regarding [solution/answer]. [Any additional actions if applicable]."
            """)
    String summarize(@UserMessage String conversationHistory);
}
