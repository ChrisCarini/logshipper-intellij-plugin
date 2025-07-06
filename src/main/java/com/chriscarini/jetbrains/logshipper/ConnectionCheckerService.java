package com.chriscarini.jetbrains.logshipper;

import com.chriscarini.jetbrains.logshipper.configuration.SettingsManager;
import com.intellij.concurrency.JobScheduler;
import com.intellij.ide.AppLifecycleListener;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBusConnection;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * A service used to generate sample log messages.
 */
public class ConnectionCheckerService implements Disposable {
    private static final Logger LOG = Logger.getInstance(ConnectionCheckerService.class);

    private static final long CHECK_FREQUENCY = 5; // frequency for checking connection status

    private ScheduledFuture<?> connectionCheckJob;

    /**
     * Default constructor. Will check that the respective setting ({@code generateSampleLogMessages} is enabled
     * before starting the log message generation job.
     */
    @SuppressWarnings("this-escape")
    public ConnectionCheckerService() {
        Disposer.register(LogshipperPluginDisposable.getInstance(), this);
        this.initComponent();
    }

    public static ConnectionCheckerService getInstance() {
        return ApplicationManager.getApplication().getService(ConnectionCheckerService.class);
    }

    /**
     * Initialize this component. Will check that the respective setting ({@code generateSampleLogMessages} is enabled
     * before starting the log message generation job.
     */
    public void initComponent() {
        final SettingsManager.Settings settings = SettingsManager.getInstance().getState();
        if (settings.generateSampleLogMessages && connectionCheckJob == null) {
            this.connectionCheckJob = JobScheduler.getScheduler()
                    .scheduleWithFixedDelay(this::checkConnection, CHECK_FREQUENCY, CHECK_FREQUENCY, TimeUnit.MINUTES);

            // Subscribe to appWillBeClosed event to stop the log message job
            // we cannot do this disposeComponent as services seem to get killed too fast (before dispose but after appClosing)
            final MessageBusConnection connection =
                    ApplicationManager.getApplication().getMessageBus().connect(LogshipperPluginDisposable.getInstance());
            connection.subscribe(AppLifecycleListener.TOPIC, new AppLifecycleListener() {
                @Override
                public void appWillBeClosed(final boolean isRestart) {
                    cancelConnectionCheckJob();
                }
            });
        }
    }

    @Override
    public void dispose() {
        LOG.debug("Disposing ConnectionCheckerService...");
        cancelConnectionCheckJob();
    }

    /**
     * Cancel the existing log message job.
     */
    public void cancelConnectionCheckJob() {
        if (connectionCheckJob != null) {
            connectionCheckJob.cancel(false);
            connectionCheckJob = null;
        }
    }

    private void checkConnection() {
        if (ConnectionUtils.isConnectable()) {
            // If we're able to connect to the port, init the LogstashLayoutAppenderService to add a handler for logstash.
            LogstashLayoutAppenderService.getInstance().init();
        } else {
            // Otherwise, remove an existing logstash handlers, should one exist.
            LogstashLayoutAppenderService.getInstance().cleanupHandler();
        }
    }
}