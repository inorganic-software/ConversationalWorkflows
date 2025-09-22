package com.inorganic;

import java.io.File;
import java.net.URL;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class WireMockInitializer {
    private WireMockServer wireMockServer;

    @ConfigProperty(name = "wiremock.port", defaultValue = "8089")
    int wiremockPort;

    public void onStart(@Observes StartupEvent ev)  {
        URL resource = Thread.currentThread().getContextClassLoader().getResource("wiremock");
        if (resource == null) {
            throw new IllegalStateException("Could not find wiremock directory in classpath");
        }
        File file = new File(resource.getFile());

        wireMockServer = new WireMockServer(
                WireMockConfiguration.options()
                        .port(wiremockPort)
                        .usingFilesUnderDirectory(file.getAbsolutePath())
        );
        wireMockServer.start();
    }

    public void onStop(@Observes io.quarkus.runtime.ShutdownEvent ev) {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }
}
