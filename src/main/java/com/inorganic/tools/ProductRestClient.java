package com.inorganic.tools;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/orgs/orange/base_products/products")
@RegisterRestClient(configKey = "wiremock")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ProductRestClient {

    @POST
    @Path("/search")
    Response searchProducts(ProductTools.ProductSearchRequest request);
}