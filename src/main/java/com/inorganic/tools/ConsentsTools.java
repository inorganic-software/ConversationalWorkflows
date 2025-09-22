package com.inorganic.tools;

import static java.net.HttpURLConnection.HTTP_OK;

import java.io.StringReader;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import dev.langchain4j.agent.tool.Tool;

@ApplicationScoped
public class ConsentsTools {
    
    private static final Logger LOG = Logger.getLogger(ConsentsTools.class);
    
    @RestClient
    ConsentsRestClient consentsRestClient;

    @Tool(name = "get-accepted-consents-for-a-customer")
    public JsonObject getAcceptedConsents(String customerId) {
        LOG.info("[ConsentsTools] - getAcceptedConsents | Fetching accepted consents for customerId: " + customerId);
        return fetchConsents(customerId, "'PORTFOLIO'", "'choice=ACCEPTED'");
    }

    @Tool(name = "get-pending-consents-for-a-customer")
    public JsonObject getPendingConsents(String customerId) {
        LOG.info("[ConsentsTools] - getPendingConsents | Fetching pending consents for customerId: " + customerId);
        return fetchConsents(customerId, "'PORTFOLIO'", "'choice=PENDING'");
    }

    @Tool(name = "get-rejected-consents-for-a-customer")
    public JsonObject getRejectedConsents(String customerId) {
        LOG.info("[ConsentsTools] - getRejectedConsents | Fetching rejected consents for customerId: " + customerId);
        return fetchConsents(customerId, "'PORTFOLIO'", "'choice=REJECTED'");
    }

    private JsonObject fetchConsents(String customerId, String view, String choice) {
        try {
            Response response = consentsRestClient.getConsents(customerId, view, choice);
            if (response.getStatus() == HTTP_OK) {
                String json = response.readEntity(String.class);
                JsonObject result = Json.createReader(new StringReader(json)).readObject();
                LOG.info("[ConsentsTools] - fetchConsents | Successfully retrieved consents for customerId: " + customerId);
                return result;
            } else {
                LOG.warn("[ConsentsTools] - fetchConsents | Unexpected status: " + response.getStatus() + " for customerId: " + customerId);
                return Json.createObjectBuilder().add("error", "Unexpected status: " + response.getStatus()).build();
            }

        } catch (Exception e) {
            LOG.error("[ConsentsTools] - fetchConsents | Error calling consents API for customerId: " + customerId + ", error: " + e.getMessage());
            return Json.createObjectBuilder().add("error", "Error calling consents API: " + e.getMessage()).build();
        }
    }
}
