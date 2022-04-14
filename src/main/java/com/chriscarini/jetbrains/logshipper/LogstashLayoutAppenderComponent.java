package com.chriscarini.jetbrains.logshipper;

import com.chriscarini.jetbrains.logshipper.configuration.SettingsManager;
import com.intellij.ide.AppLifecycleListener;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.ShutDownTracker;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.logging.Handler;


/**
 * A component that creates a {@link LogstashJSONSockerHandler} and adds it as a handler to the
 * root {@link java.util.logging.Logger} for the IntelliJ IDE.
 */
public class LogstashLayoutAppenderComponent implements AppLifecycleListener, Disposable {
    private static final Logger LOG = Logger.getInstance(LogstashLayoutAppenderComponent.class);

    public static LogstashLayoutAppenderComponent getInstance() {
        return ApplicationManager.getApplication().getComponent(LogstashLayoutAppenderComponent.class);
    }

    private LogstashJSONSockerHandler handler = null;

    @Override
    public void appFrameCreated(@NotNull List<String> commandLineArgs) {
        init();
    }

    public LogstashLayoutAppenderComponent() {
        Disposer.register(ApplicationManager.getApplication(), this);
    }

    public void init() {
        // Add a handler to the root logger; this handler will log to logstash.
        final SettingsManager.Settings settings = SettingsManager.getInstance().getState();

        if (!StringUtil.isEmpty(settings.hostname) && !StringUtil.isEmpty(settings.port)) {
            // Clean up any exiting handlers
            cleanupHandler();

            // Create a new handler with the most recent information from the settings
            try {
                this.handler = new LogstashJSONSockerHandler(settings.hostname, Integer.parseInt(settings.port), settings.includeLocationInformation, this::cleanupHandler);
            } catch (java.net.ConnectException e) {
                LOG.info(String.format("Connection exception when connecting to %s:%s - please restart the IDE and ensure the hostname and port are correct", settings.hostname, settings.port), e);
                return;
            } catch (IOException e) {
                LOG.info("IOException encountered when trying to create the LogstashJSONSockerHandler. Please report this issue to the maintainer.", e);
                return;
            }

            // Add the handler to the root Logger
            getRootLogger().addHandler(this.handler);

            // Register a shutdown task to remove the handler and close it cleanly.
            ShutDownTracker.getInstance().registerShutdownTask(this::cleanupHandler);

            LOG.info("Added Logshipper handler to root logger");
        } else {
            LOG.info("Logshipper hostname / port is empty, please configure this in the settings.");
        }
    }

    /**
     * Cleanup the handler, removing it from the root logger, and calling its {@link Handler#close()} method.
     */
    private void cleanupHandler() {
        if (this.handler != null) {
            getRootLogger().removeHandler(this.handler);
            this.handler.close();
            this.handler = null;
        }
    }

    private java.util.logging.Logger getRootLogger() {
        return java.util.logging.Logger.getLogger("");
    }

    @Override
    public void dispose() {
        Disposer.dispose(this);
    }
}
