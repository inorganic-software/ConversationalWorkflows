package com.inorganic.tools;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;

import dev.langchain4j.agent.tool.Tool;

@ApplicationScoped
public class SubscriptionsTools {
    
    private static final Logger LOG = Logger.getLogger(SubscriptionsTools.class);

    @RestClient
    SubscriptionsRestClient subscriptionsRestClient;

    public record SubscriptionResponse(
            List<SubscriptionItem> items
    ) {

        public record SubscriptionItem(
                @JsonProperty("last_status_reason") String lastStatusReason,
                String status,
                String type
        ) { }
    }

    @Tool(name = "retrieve-subscriptions")
    public List<SubscriptionResponse.SubscriptionItem> getSubscriptions(String accountId) {
        LOG.info("[SubscriptionsTools] - getSubscriptions | Fetching subscriptions for accountId: " + accountId);
        try {
            SubscriptionResponse response = subscriptionsRestClient.getSubscriptions(accountId);
            List<SubscriptionResponse.SubscriptionItem> items = response.items();
            LOG.info("[SubscriptionsTools] - getSubscriptions | Successfully retrieved " + (items != null ? items.size() : 0) + " subscriptions for accountId: " + accountId);
            return items;
        } catch (Exception e) {
            LOG.error("[SubscriptionsTools] - getSubscriptions | Error fetching subscriptions for accountId: " + accountId + ", error: " + e.getMessage());
            throw e;
        }
    }

}
