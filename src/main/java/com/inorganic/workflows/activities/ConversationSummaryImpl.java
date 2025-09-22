package com.inorganic.workflows.activities;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

@ApplicationScoped
public class ConversationSummaryImpl implements ConversationSummary {
    
    private static final Logger LOG = Logger.getLogger(ConversationSummaryImpl.class);
    
    @Inject
    ConversationSummaryPrompt conversationSummaryPrompt;
    
    @Override
    public String summarizeConversation(String conversationHistory) {
        LOG.info("[ConversationSummaryImpl] - summarizeConversation | Starting conversation summary generation with history length: " + (conversationHistory != null ? conversationHistory.length() : 0));
        try {
            String summary = conversationSummaryPrompt.summarize(conversationHistory);
            LOG.info("[ConversationSummaryImpl] - summarizeConversation | Successfully generated conversation summary");
            return summary;
        } catch (Exception e) {
            LOG.error("[ConversationSummaryImpl] - summarizeConversation | Error generating conversation summary: " + e.getMessage(), e);
            return "Error al generar el resumen de la conversación";
        }
    }
}