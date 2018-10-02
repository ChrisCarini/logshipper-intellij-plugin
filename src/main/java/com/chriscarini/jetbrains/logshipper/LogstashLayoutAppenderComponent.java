package com.chriscarini.jetbrains.logshipper;

import com.chriscarini.jetbrains.logshipper.configuration.SettingsManager;
import com.chriscarini.jetbrains.logshipper.logger.LayoutSocketAppender;
import com.chriscarini.jetbrains.logshipper.logger.LogstashJSONSocketAppender;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.ShutDownTracker;
import com.intellij.openapi.util.text.StringUtil;
import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.jetbrains.annotations.NotNull;

/**
 * An {@link ApplicationComponent} that creates a {@link LogstashJSONSocketAppender} and adds it as an appender to the
 * root {@link org.apache.log4j.Logger} for the IntelliJ IDE.
 */
public class LogstashLayoutAppenderComponent implements ApplicationComponent {
    private static final Logger LOG = Logger.getInstance(LogstashLayoutAppenderComponent.class);

    private static final String COMPONENT_ID = "com.chriscarini.jetbrains.logshipper.LogstashLayoutAppenderComponent";

    /**
     * Initalize this component, registering the appender if the host & port were specified in the settings window.
     */
    @Override
    public void initComponent() {
        // Add an appender to the root logger; this appender will log to logstash.
        final SettingsManager.Settings settings = SettingsManager.getInstance().getState();

        final int reconnectionDelay = settings.reconnectDelay == null || settings.reconnectDelay.isEmpty()
                ? LayoutSocketAppender.DEFAULT_RECONNECT_DELAY : Integer.decode(settings.reconnectDelay);
        final boolean includeLocationInfo = settings.includeLocationInformation;

        if (!StringUtil.isEmpty(settings.hostname) && !StringUtil.isEmpty(settings.port)) {
            final Appender appender =
                    new LogstashJSONSocketAppender(settings.hostname, Integer.decode(settings.port), reconnectionDelay,
                            includeLocationInfo);

            // Add the appender to the root Logger
            LogManager.getRootLogger().addAppender(appender);

            // Register a shutdown task to remove the appender and close it cleanly.
            ShutDownTracker.getInstance().registerShutdownTask(() -> {
                LogManager.getRootLogger().removeAppender(appender);
                appender.close();
            });
            LOG.info("Added Logshipper appender to root logger");
        } else {
            LOG.info("Logshipper hostname / port is empty, please configure this in the settings.¬");
        }
    }

    @NotNull
    @Override
    public String getComponentName() {
        return COMPONENT_ID;
    }
}
