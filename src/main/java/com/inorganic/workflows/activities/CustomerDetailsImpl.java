package com.inorganic.workflows.activities;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

import java.io.StringReader;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.inorganic.tools.CustomerRestClient;
import com.inorganic.tools.exceptions.ToolException;

import io.temporal.failure.ApplicationFailure;

@ApplicationScoped
public class CustomerDetailsImpl implements CustomerDetails {
    
    private static final Logger LOG = Logger.getLogger(CustomerDetailsImpl.class);

    @Inject
    CustomerDetailsPrompt promt;

    @RestClient
    CustomerRestClient customerRestClient;

    @Override
    public String retrieveCustomerDetails(boolean hasGreeted, String customerId, String userName, String message, String conversationHistory) {
        LOG.info("[CustomerDetailsImpl] - retrieveCustomerDetails | Processing customer details request for customerId: " + customerId + ", userName: " + userName);
        try {
            String response = promt.retrieveCustomer(customerId, userName, hasGreeted, message, conversationHistory);
            LOG.info("[CustomerDetailsImpl] - retrieveCustomerDetails | Successfully retrieved customer details for customerId: " + customerId);
            return response;
        } catch (ToolException e) {
            LOG.error("[CustomerDetailsImpl] - retrieveCustomerDetails | ToolException occurred: " + e.getMessage());
            throw ApplicationFailure.newFailure(e.getMessage(), e.getErrorType());
        } catch (Exception e) {
            LOG.error("[CustomerDetailsImpl] - retrieveCustomerDetails | Failed to retrieve customer details for customerId: " + customerId + ", error: " + e.getMessage());
            throw ApplicationFailure.newFailure("Failed to retrieve customer details using prompt: " + e.getMessage(), "PromptRetrievalFailure", e);
        }
    }

    @Override
    public String retrieveCustomerName(String customerId) {
        LOG.info("[CustomerDetailsImpl] - retrieveCustomerName | Retrieving customer name for customerId: " + customerId);
        try {
            Response response = customerRestClient.getCustomerDetails(customerId);
            if (response.getStatus() == HTTP_OK) {
                String json = response.readEntity(String.class);
                try (JsonReader reader = Json.createReader(new StringReader(json))) {
                    JsonObject root = reader.readObject();
                    JsonObject personalInfo = root.getJsonObject("personal_info");

                    if (personalInfo != null && personalInfo.containsKey("name")) {
                        String name = personalInfo.getString("name");
                        LOG.info("[CustomerDetailsImpl] - retrieveCustomerName | Successfully retrieved name: " + name + " for customerId: " + customerId);
                        return name;
                    } else {
                        LOG.warn("[CustomerDetailsImpl] - retrieveCustomerName | 'name' field is missing in 'personal_info' for customerId: " + customerId);
                        throw ApplicationFailure.newFailure("'name' field is missing in 'personal_info'", "MissingNameField");
                    }
                } catch (Exception e) {
                    LOG.error("[CustomerDetailsImpl] - retrieveCustomerName | Failed to parse JSON for customerId: " + customerId + ", error: " + e.getMessage());
                    throw ApplicationFailure.newFailure("Failed to parse JSON from customer details: " + e.getMessage(), "JsonParseError");
                }

            } else if (response.getStatus() == HTTP_NOT_FOUND) {
                LOG.error("[CustomerDetailsImpl] - retrieveCustomerName | Customer not found (404) for customerId: " + customerId);
                throw ApplicationFailure.newFailure("Customer not found for ID: " + customerId, "CustomerNotFound");
            } else {
                LOG.error("[CustomerDetailsImpl] - retrieveCustomerName | Unexpected HTTP status: " + response.getStatus() + " for customerId: " + customerId);
                throw ApplicationFailure.newFailure("Unexpected HTTP status: " + response.getStatus(), "UnexpectedHttpStatus");
            }
        } catch (ToolException e) {
            LOG.error("[CustomerDetailsImpl] - retrieveCustomerName | ToolException occurred: " + e.getMessage());
            throw ApplicationFailure.newFailure(e.getMessage(), e.getErrorType());
        } catch (Exception e) {
            LOG.error("[CustomerDetailsImpl] - retrieveCustomerName | Unexpected exception occurred: " + e.getMessage());
            throw ApplicationFailure.newFailure("Unexpected error while retrieving customer name: " + e.getMessage(), "UnexpectedError", e);
        }
    }
}
