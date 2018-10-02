package com.chriscarini.jetbrains.logshipper;

import com.chriscarini.jetbrains.logshipper.configuration.SettingsManager;
import com.intellij.concurrency.JobScheduler;
import com.intellij.ide.AppLifecycleListener;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * An application component used to generate sample log messages.
 */
public class ConstantLogEntryTesterComponent implements ApplicationComponent {
    private static final Logger LOG = Logger.getInstance(ConstantLogEntryTesterComponent.class);

    private static final String COMPONENT_ID = "com.chriscarini.jetbrains.logshipper.ConstantLogEntryTesterComponent";

    private static final long LOG_FREQUENCY = 2; // frequency for adding a log message to IDE logs.

    private ScheduledFuture<?> logMessageJob;

    public static ConstantLogEntryTesterComponent getInstance() {
        return ApplicationManager.getApplication().getComponent(ConstantLogEntryTesterComponent.class);
    }

    /**
     * Initialize this component. Will check that the respective setting ({@code generateSampleLogMessages} is enabled
     * before starting the log message generation job.
     */
    public void initComponent() {
        final SettingsManager.Settings settings = SettingsManager.getInstance().getState();
        if (settings.generateSampleLogMessages && logMessageJob == null) {
            this.logMessageJob = JobScheduler.getScheduler()
                    .scheduleWithFixedDelay(this::logTime, LOG_FREQUENCY, LOG_FREQUENCY, TimeUnit.SECONDS);
            // Subscribe to appWillBeClosed event to emit shutdown metric
            // we cannot do this disposeComponent as services seem to get killed too fast (before dispose but after appClosing)
            final Application app = ApplicationManager.getApplication();
            final MessageBusConnection connection = app.getMessageBus().connect(app);
            connection.subscribe(AppLifecycleListener.TOPIC, new AppLifecycleListener() {
                @Override
                public void appWillBeClosed(final boolean isRestart) {
                    cancelLogMessageJob();
                }
            });
        }
    }

    /**
     * Cancel the existing log message job.
     */
    public void cancelLogMessageJob() {
        if (logMessageJob != null) {
            logMessageJob.cancel(false);
            logMessageJob = null;
        }
    }

    private void logTime() {
        final SettingsManager.Settings settings = SettingsManager.getInstance().getState();
        if (settings.generateSampleLogMessages) {
            LOG.info(String.format("It is currently: %s", new Date().toString()));
        }
    }

    @NotNull
    @Override
    public String getComponentName() {
        return COMPONENT_ID;
    }
}