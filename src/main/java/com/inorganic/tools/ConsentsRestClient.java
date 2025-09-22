package com.inorganic.tools;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/v2/orgs/orange/customers")
@RegisterRestClient(configKey = "wiremock")
@Produces(MediaType.APPLICATION_JSON)
public interface ConsentsRestClient {

    @GET
    @Path("/{customerId}/consents")
    Response getConsents(
            @PathParam("customerId") String customerId,
            @QueryParam("view") String view,
            @QueryParam("choice") String choice
    );
}
