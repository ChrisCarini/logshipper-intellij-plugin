package com.chriscarini.jetbrains.logshipper;

import com.chriscarini.jetbrains.logshipper.configuration.SettingsManager;
import com.chriscarini.jetbrains.logshipper.logger.LayoutSocketAppender;
import com.chriscarini.jetbrains.logshipper.logger.LogstashJSONSocketAppender;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.ShutDownTracker;
import com.intellij.openapi.util.text.StringUtil;
import org.apache.log4j.Appender;
import org.apache.log4j.LogManager;
import org.jetbrains.annotations.NotNull;


/**
 * A component that creates a {@link LogstashJSONSocketAppender} and adds it as an appender to the
 * root {@link org.apache.log4j.Logger} for the IntelliJ IDE.
 */
public class LogstashLayoutAppenderComponent {
  private static final Logger LOG = Logger.getInstance(LogstashLayoutAppenderComponent.class);

  /**
   * Default constructor. Registering the appender if the host & port were specified in the settings window.
   */
  public LogstashLayoutAppenderComponent() {
    // Add an appender to the root logger; this appender will log to logstash.
    final SettingsManager.Settings settings = SettingsManager.getInstance().getState();

    final int reconnectionDelay = settings.reconnectDelay == null || settings.reconnectDelay.isEmpty()
        ? LayoutSocketAppender.DEFAULT_RECONNECT_DELAY : Integer.decode(settings.reconnectDelay);
    final boolean includeLocationInfo = settings.includeLocationInformation;

    if (!StringUtil.isEmpty(settings.hostname) && !StringUtil.isEmpty(settings.port)) {
      final Appender appender =
          new LogstashJSONSocketAppender(settings.hostname, Integer.decode(settings.port), reconnectionDelay,
              includeLocationInfo, this::cleanupAppender);

      // Add the appender to the root Logger
      LogManager.getRootLogger().addAppender(appender);

      // Register a shutdown task to remove the appender and close it cleanly.
      ShutDownTracker.getInstance().registerShutdownTask(() -> {
        cleanupAppender(appender);
      });
      LOG.info("Added Logshipper appender to root logger");
    } else {
      LOG.info("Logshipper hostname / port is empty, please configure this in the settings.");
    }
  }

  /**
   * CLeanup the provided appender, removing it from the root logger, and calling its {@link Appender#close()} method.
   * @param thisAppender The {@link Appender} to cleanup.
   */
  private void cleanupAppender(@NotNull final Appender thisAppender) {
    LogManager.getRootLogger().removeAppender(thisAppender);
    thisAppender.close();
  }
}
