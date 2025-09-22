package com.inorganic.workflows.activities;

import jakarta.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;

@ApplicationScoped
public class SessionCleanupImpl implements SessionCleanup {

    private static final Logger LOG = Logger.getLogger(SessionCleanupImpl.class);

    @Override
    public void cleanupUserSession(String userEmail) {
        LOG.info("[SessionCleanupImpl] - cleanupUserSession | Session cleanup completed for user: " + userEmail + " (stateless - no local cache to clean)");
        // Note: With stateless architecture, there's no local cache to clean up.
        // The workflow persists in Temporal and will handle its own cleanup when it times out.
    }
}