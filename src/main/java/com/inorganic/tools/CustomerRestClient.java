package com.inorganic.tools;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/v4/orgs/orange/customers")
@RegisterRestClient(configKey = "wiremock")
@Produces(MediaType.APPLICATION_JSON)
public interface CustomerRestClient {

    @GET
    @Path("/search")
    Response searchCustomer(@QueryParam("email") String email);

    @GET
    @Path("/{customerId}")
    Response getCustomerDetails(@jakarta.ws.rs.PathParam("customerId") String customerId);
}
