package com.inorganic.tools;

import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/orgs/orange/stock")
@RegisterRestClient(configKey = "wiremock")
@Produces(MediaType.APPLICATION_JSON)
public interface StockRestClient {

    @GET
    List<StockTools.Stock> getStock();
}
