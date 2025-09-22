package com.inorganic.tools;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
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
public class CustomerTools {
    
    private static final Logger LOG = Logger.getLogger(CustomerTools.class);

    @RestClient
    CustomerRestClient customerRestClient;

    @Tool(name = "get-full-customer-details-by-ID")
    public JsonObject getCustomerDetails(String customerId) {
        LOG.info("[CustomerTools] - getCustomerDetails | Fetching customer details for customerId: " + customerId);
        try {
            Response response = customerRestClient.getCustomerDetails(customerId);
            if (response.getStatus() == HTTP_OK) {
                String json = response.readEntity(String.class);
                JsonObject result = Json.createReader(new StringReader(json)).readObject();
                LOG.info("[CustomerTools] - getCustomerDetails | Successfully retrieved customer details for customerId: " + customerId);
                return result;

            } else if (response.getStatus() == HTTP_NOT_FOUND) {
                LOG.warn("[CustomerTools] - getCustomerDetails | Customer not found (404) for customerId: " + customerId);
                return Json.createObjectBuilder().add("error", "Customer not found (404)").build();

            } else {
                LOG.error("[CustomerTools] - getCustomerDetails | Unexpected status: " + response.getStatus() + " for customerId: " + customerId);
                return Json.createObjectBuilder().add("error", "Unexpected status: " + response.getStatus()).build();
            }

        } catch (Exception e) {
            LOG.error("[CustomerTools] - getCustomerDetails | Error fetching customer details for customerId: " + customerId + ", error: " + e.getMessage());
            return Json.createObjectBuilder().add("error", "Error calling getCustomerDetails: " + e.getMessage()).build();
        }
    }
}
