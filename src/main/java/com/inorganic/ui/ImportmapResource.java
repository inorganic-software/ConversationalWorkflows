package com.inorganic.ui;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import org.jboss.logging.Logger;

import io.mvnpm.importmap.Aggregator;
import io.mvnpm.importmap.model.Imports;
import io.quarkus.runtime.annotations.RegisterForReflection;

@ApplicationScoped
@Path("/_importmap")
@RegisterForReflection(targets = {Aggregator.class, Imports.class})
public class ImportmapResource {
    
    private static final Logger LOG = Logger.getLogger(ImportmapResource.class);
    private String importmap;

    // See https://github.com/WICG/import-maps/issues/235
    @GET
    @Path("/dynamic.importmap")
    @Produces("application/importmap+json")
    public String importMap() {
        LOG.debug("[ImportmapResource] - importMap | Serving dynamic importmap");
        return this.importmap;
    }

    @GET
    @Path("/dynamic-importmap.js")
    @Produces("application/javascript")
    public String importMapJson() {
        LOG.debug("[ImportmapResource] - importMapJson | Serving dynamic importmap as JavaScript");
        return JAVASCRIPT_CODE.formatted(this.importmap);
    }

    @PostConstruct
    void init() {
        LOG.info("[ImportmapResource] - init | Initializing importmap aggregator");
        Aggregator aggregator = new Aggregator();
        aggregator.addMapping("icons/", "/icons/");
        aggregator.addMapping("components/", "/components/");
        aggregator.addMapping("fonts/", "/fonts/");
        this.importmap = aggregator.aggregateAsJson();
        LOG.info("[ImportmapResource] - init | Importmap initialization completed");
    }

    private static final String JAVASCRIPT_CODE = """
            const im = document.createElement('script');
            im.type = 'importmap';
            im.textContent = JSON.stringify(%s);
            document.currentScript.after(im);
            """;
}
