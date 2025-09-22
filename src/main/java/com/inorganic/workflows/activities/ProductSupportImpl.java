package com.inorganic.workflows.activities;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;

import org.jboss.logging.Logger;

import com.inorganic.tools.exceptions.ToolException;

import io.temporal.failure.ApplicationFailure;

@ApplicationScoped
public class ProductSupportImpl implements ProductSupport{
    
    private static final Logger LOG = Logger.getLogger(ProductSupportImpl.class);

    @Inject
    ProductSupportPrompt productSupportPrompt;

    @Override
    public String retrieveProducts(boolean hasGreeted, String userId, String userName, String message, String conversationHistory) {
        LOG.info("[ProductSupportImpl] - retrieveProducts | Processing products request for userId: " + userId + ", userName: " + userName);
        try {
            String response = productSupportPrompt.retrieveProducts(userId, userName, hasGreeted, message, conversationHistory);
            LOG.info("[ProductSupportImpl] - retrieveProducts | Successfully retrieved products for userId: " + userId);
            return response;
        } catch (ToolException e) {
            LOG.error("[ProductSupportImpl] - retrieveProducts | ToolException occurred: " + e.getMessage());
            throw ApplicationFailure.newFailure(e.getMessage(), e.getErrorType());
        } catch (Exception e) {
            LOG.error("[ProductSupportImpl] - retrieveProducts | Error retrieving products for userId: " + userId + ", error: " + e.getMessage());
            throw ApplicationFailure.newFailure("Error retrieving products for userId: " + userId + ", userName: " + userName, "RetrieveProductsFailure", e);
        }
    }

    public String retrieveProductId(String userId, String userName, String message, String conversationHistory) {
        LOG.info("[ProductSupportImpl] - retrieveProductId | Processing product ID request for userId: " + userId + ", userName: " + userName);
        try {
            String jsonArray = Json.createArrayBuilder()
                    .add(userId)
                    .add(userName)
                    .add(message)
                    .add(conversationHistory != null ? conversationHistory : "")
                    .build()
                    .toString();

            String productId = productSupportPrompt.getProductId(jsonArray);
            LOG.info("[ProductSupportImpl] - retrieveProductId | Successfully retrieved productId: " + productId + " for userId: " + userId);
            return productId;
        } catch (ToolException e) {
            LOG.error("[ProductSupportImpl] - retrieveProductId | ToolException occurred: " + e.getMessage());
            throw ApplicationFailure.newFailure(e.getMessage(), e.getErrorType());
        } catch (Exception e) {
            LOG.error("[ProductSupportImpl] - retrieveProductId | Error retrieving productId for userId: " + userId + ", error: " + e.getMessage());
            throw ApplicationFailure.newFailure("Error retrieving productId for userId: " + userId + ", userName: " + userName, "RetrieveProductIdFailure", e);
        }
    }
}
