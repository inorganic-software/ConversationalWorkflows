package com.inorganic.workflows.activities;

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

import com.inorganic.tools.AccountRestClient;
import com.inorganic.tools.exceptions.ToolException;

import io.temporal.failure.ApplicationFailure;

@ApplicationScoped
public class AccountDetailsImpl implements AccountDetails {
    
    private static final Logger LOG = Logger.getLogger(AccountDetailsImpl.class);
    private static final int FIRST_ACCOUNT = 0;

    @RestClient
    AccountRestClient accountRestClient;

    @Override
    public String getAccountByCustomerId(String customerId) {
        LOG.info("[AccountDetailsImpl] - getAccountByCustomerId | Starting account retrieval for customerId: " + customerId);
        try {
            Response response = accountRestClient.getAccounts(customerId);
            if (response.getStatus() == HTTP_OK) {
                String json = response.readEntity(String.class);
                try (JsonReader reader = Json.createReader(new StringReader(json))) {
                    JsonObject root = reader.readObject();

                    if (!root.containsKey("accounts") || root.getJsonArray("accounts").isEmpty()) {
                        LOG.warn("[AccountDetailsImpl] - getAccountByCustomerId | No accounts found for customerId: " + customerId);
                        throw ApplicationFailure.newFailure("No accounts found for customerId: " + customerId, "EmptyAccounts");
                    }

                    JsonObject firstAccount = root.getJsonArray("accounts").getJsonObject(FIRST_ACCOUNT);
                    String accountId = firstAccount.getString("id");
                    LOG.info("[AccountDetailsImpl] - getAccountByCustomerId | Successfully retrieved accountId: " + accountId + " for customerId: " + customerId);
                    return accountId;
                }
            } else if (response.getStatus() == HTTP_NOT_FOUND) {
                LOG.error("[AccountDetailsImpl] - getAccountByCustomerId | Account not found (404) for customerId: " + customerId);
                throw ApplicationFailure.newFailure("Account not found for customerId: " + customerId, "NotFound");
            } else {
                LOG.error("[AccountDetailsImpl] - getAccountByCustomerId | Unexpected HTTP status: " + response.getStatus() + " for customerId: " + customerId);
                throw ApplicationFailure.newFailure("Unexpected HTTP status " + response.getStatus() + " while fetching accounts for customerId: " + customerId, "HttpError");
            }
        } catch (ToolException e) {
            LOG.error("[AccountDetailsImpl] - getAccountByCustomerId | ToolException occurred while retrieving account for customerId: " + customerId + ", error: " + e.getMessage());
            throw ApplicationFailure.newFailure(e.getMessage(), e.getErrorType());
        } catch (Exception e) {
            LOG.error("[AccountDetailsImpl] - getAccountByCustomerId | Exception occurred while retrieving account for customerId: " + customerId + ", error: " + e.getMessage());
            throw ApplicationFailure.newFailure("Error retrieving account for customerId: " + customerId + " - " + e.getMessage(), "RestClientException");
        }
    }
}
