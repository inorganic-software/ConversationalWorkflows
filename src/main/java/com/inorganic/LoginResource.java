package com.inorganic;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.logging.Logger;

import io.quarkus.oidc.AccessTokenCredential;
import io.quarkus.oidc.UserInfo;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.quarkus.security.Authenticated;

@Path("/login")
@Authenticated
public class LoginResource {
    
    private static final Logger LOG = Logger.getLogger(LoginResource.class);

    @Inject
    UserInfo userInfo;

    @Inject
    AccessTokenCredential accessToken;

    @Inject
    Template home;

    @GET
    @Produces("text/html")
    public TemplateInstance setupLoginPage() {
        LOG.info("[LoginResource] - setupLoginPage | Processing login page request");
        String userName = userInfo.getName();
        TemplateInstance response = home.data("name", userName).data("accessToken", accessToken.getToken());
        LOG.info("[LoginResource] - setupLoginPage | Successfully generated login page for user: " + userName);
        return response;
    }
}
