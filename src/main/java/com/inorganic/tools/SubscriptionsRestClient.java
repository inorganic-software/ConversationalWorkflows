package com.inorganic.tools;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/v2/orgs/yoigo/subscriptions")
@RegisterRestClient(configKey = "wiremock")
@Produces(MediaType.APPLICATION_JSON)
public interface SubscriptionsRestClient {

    @GET
    SubscriptionsTools.SubscriptionResponse getSubscriptions(@QueryParam("account_id") String accountId);
}
