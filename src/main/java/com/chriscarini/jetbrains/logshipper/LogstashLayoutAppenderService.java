package com.chriscarini.jetbrains.logshipper;

import com.chriscarini.jetbrains.logshipper.configuration.SettingsManager;
import com.intellij.ide.AppLifecycleListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ex.ApplicationInfoEx;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.platform.ide.bootstrap.StartupUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.logging.Handler;


/**
 * A service that creates a {@link LogstashJSONSockerHandler} and adds it as a handler to the
 * root {@link java.util.logging.Logger} for the IntelliJ IDE.
 */
public class LogstashLayoutAppenderService implements AppLifecycleListener {
    private static final Logger LOG = Logger.getInstance(LogstashLayoutAppenderService.class);

    public static LogstashLayoutAppenderService getInstance() {
        return ApplicationManager.getApplication().getService(LogstashLayoutAppenderService.class);
    }

    private LogstashJSONSockerHandler handler = null;

    @Override
    public void appFrameCreated(@NotNull List<String> commandLineArgs) {
        init();
    }

    public LogstashLayoutAppenderService() {}

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

            LOG.info("Adding Logshipper handler to root logger");
            // Add the handler to the root Logger
            getRootLogger().addHandler(this.handler);

            // NOTE: `ShutDownTracker` class was marked with `@ApiStatus.Internal` on 2024-07-17 in 
            // https://github.com/JetBrains/intellij-community/commit/ef82709 - as a possible workaround, 
            // we could try registering a {@link Runtime#addShutdownHook(Thread)} directly. Instead of doing
            // that, on 2025-01-18, in debug mode, I noticed that this shutdown task is actually invoked 
            // *AFTER* `this.handler` is cleaned up (from the `disposableConsumer` of `LogstashJSONSockerHandler`)
            // which is likely being 
            // As a byproduct, I'm going to opt to just XX
            //  // Register a shutdown task to remove the handler and close it cleanly.
            //  // noinspection UnstableApiUsage
            //  ShutDownTracker.getInstance().registerShutdownTask(this::cleanupHandler);
            
            // Once we're attached to the root logger, log out the same essential information 
            // about the IDE as normally happens during IDE startup.
            StartupUtil.logEssentialInfoAboutIde(LOG, ApplicationInfoEx.getInstanceEx(), List.of(""));

            LOG.info("Added Logshipper handler to root logger");
        } else {
            LOG.info("Logshipper hostname / port is empty, please configure this in the settings.");
        }

        // We call to the ConstantLogEntryTesterService to ensure that it is created & running, since IJ services are loaded 'on demand'.
        // Ideally, we'd just want this service to start right away via plugin.xml, but that's no longer an option.
        ConstantLogEntryTesterService.getInstance();
    }

    /**
     * Cleanup the handler, removing it from the root logger, and calling its {@link Handler#close()} method.
     */
    private void cleanupHandler() {
        if (this.handler != null) {
            LOG.info("Removing Logshipper handler from root logger");
            getRootLogger().removeHandler(this.handler);
            LOG.info("Closing Logshipper handler");
            this.handler.close();
            this.handler = null;
        }
    }

    private java.util.logging.Logger getRootLogger() {
        return java.util.logging.Logger.getLogger("");
    }
}
