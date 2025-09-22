package com.inorganic.tools;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

import java.io.StringReader;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.inorganic.tools.exceptions.ToolException;

import dev.langchain4j.agent.tool.Tool;

@ApplicationScoped
public class AccountTools {
    
    private static final Logger LOG = Logger.getLogger(AccountTools.class);

    @RestClient
    AccountRestClient accountRestClient;

    @Tool(name = "get-account-for-customer")
    public JsonObject getAccount(String customerId) {
        LOG.info("[AccountTools] - getAccount | Fetching account for customerId: " + customerId);
        try {
            Response response = accountRestClient.getAccounts(customerId);
            if (response.getStatus() == HTTP_OK) {
                String json = response.readEntity(String.class);
                try (JsonReader reader = Json.createReader(new StringReader(json))) {
                    JsonObject account = reader.readObject();
                    LOG.info("[AccountTools] - getAccount | Successfully retrieved account for customerId: " + customerId);
                    return account;
                }
            } else if (response.getStatus() == HTTP_NOT_FOUND) {
                LOG.error("[AccountTools] - getAccount | Account not found (404) for customerId: " + customerId);
                throw new ToolException("Account not found for customerId: " + customerId, "NotFound"
                );
            } else {
                LOG.error("[AccountTools] - getAccount | Unexpected HTTP status: " + response.getStatus() + " for customerId: " + customerId);
                throw new ToolException("Unexpected HTTP status " + response.getStatus() + " while fetching account for customerId: " + customerId, "HttpError"
                );
            }
        } catch (Exception e) {
            LOG.error("[AccountTools] - getAccount | Error fetching account for customerId: " + customerId + ", error: " + e.getMessage());
            throw new ToolException("Error calling getAccounts for customerId: " + customerId + " - " + e.getMessage(),"RestClientException");
        }
    }
}
