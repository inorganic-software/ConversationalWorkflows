package com.inorganic.workflows.activities;

import static java.net.HttpURLConnection.HTTP_OK;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import com.inorganic.tools.CustomerRestClient;
import com.inorganic.tools.exceptions.ToolException;

import io.temporal.failure.ApplicationFailure;

@ApplicationScoped
public class RetrieveCustomerIdImpl implements RetrieveCustomerId {
    
    private static final Logger LOG = Logger.getLogger(RetrieveCustomerIdImpl.class);

    public record CustomerSearchResponse(Customer customer) {
        public record Customer(String id) {
        }
    }

    @Inject
    @RestClient
    CustomerRestClient customerRestClient;

    @Override
    public String getCustomerId(String email) {
        LOG.info("[RetrieveCustomerIdImpl] - getCustomerId | Searching for customer with email: " + email);
        try {
            Response response = customerRestClient.searchCustomer(email);
            if (response.getStatus() != HTTP_OK) {
                LOG.error("[RetrieveCustomerIdImpl] - getCustomerId | Failed to fetch customer for email: " + email + ", HTTP status: " + response.getStatus());
                throw ApplicationFailure.newFailure("Failed to fetch customer by email. HTTP status: " + response.getStatus(), "CustomerSearchHttpError");
            }

            CustomerSearchResponse body = response.readEntity(CustomerSearchResponse.class);

            if (body == null) {
                LOG.error("[RetrieveCustomerIdImpl] - getCustomerId | Null response received for email: " + email);
                throw ApplicationFailure.newFailure("Null response received when searching customer by email: " + email, "NullCustomerResponse");
            }

            if (body.customer() == null || body.customer().id() == null) {
                LOG.warn("[RetrieveCustomerIdImpl] - getCustomerId | Customer not found or missing ID for email: " + email);
                throw ApplicationFailure.newFailure("Customer not found or missing ID for email: " + email, "CustomerNotFound");
            }

            String customerId = body.customer().id();
            LOG.info("[RetrieveCustomerIdImpl] - getCustomerId | Successfully retrieved customerId: " + customerId + " for email: " + email);
            return customerId;
        } catch (ToolException e) {
            LOG.error("[RetrieveCustomerIdImpl] - getCustomerId | ToolException occurred: " + e.getMessage());
            throw ApplicationFailure.newFailure(e.getMessage(), e.getErrorType());
        } catch (Exception e) {
            LOG.error("[RetrieveCustomerIdImpl] - getCustomerId | Unexpected exception occurred: " + e.getMessage());
            throw ApplicationFailure.newFailure("Unexpected error while searching customer: " + e.getMessage(), "UnexpectedError", e);
        }
    }
}
