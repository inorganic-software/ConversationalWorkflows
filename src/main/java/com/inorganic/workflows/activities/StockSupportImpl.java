package com.inorganic.workflows.activities;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

import com.inorganic.tools.exceptions.ToolException;

import io.temporal.failure.ApplicationFailure;

@ApplicationScoped
public class StockSupportImpl implements StockSupport {
    
    private static final Logger LOG = Logger.getLogger(StockSupportImpl.class);

    @Inject
    StockSupportPrompt stockSupportPrompt;

    @Override
    public String getStock(boolean hasGreeted, String request, String productId, String conversationHistory) {
        LOG.info("[StockSupportImpl] - getStock | Processing stock request for productId: " + productId);
        try {
            String stockInfo = stockSupportPrompt.retrieveStock(productId, hasGreeted, request, conversationHistory);
            LOG.info("[StockSupportImpl] - getStock | Successfully retrieved stock information for productId: " + productId);
            return stockInfo;
        } catch (ToolException e) {
            LOG.error("[StockSupportImpl] - getStock | ToolException occurred: " + e.getMessage());
            throw ApplicationFailure.newFailure(e.getMessage(), e.getErrorType());
        } catch (Exception e) {
            LOG.error("[StockSupportImpl] - getStock | Error retrieving stock for productId: " + productId + ", error: " + e.getMessage());
            throw ApplicationFailure.newFailure("Error retrieving stock for productId: " + productId, "RetrieveStockFailure", e);
        }
    }
}
