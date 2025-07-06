package com.chriscarini.jetbrains.logshipper;

import com.chriscarini.jetbrains.logshipper.logger.LogstashUtilFormatter;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.LogRecord;
import java.util.logging.SocketHandler;

public class LogstashJSONSocketHandler extends SocketHandler implements Disposable {
    private final Runnable disposableConsumer;

    @SuppressWarnings("this-escape")
    public LogstashJSONSocketHandler(@NotNull final String host, final int port, final boolean includeLocationInfo, @NotNull final Runnable disposableConsumer) throws IOException {
        super(host, port);
        Disposer.register(LogshipperPluginDisposable.getInstance(), this);
        setFormatter(new LogstashUtilFormatter(includeLocationInfo));
        // TODO(ChrisCarini) - Possible way to avoid the `@SuppressWarnings("this-escape")` annotation.
        this.disposableConsumer = disposableConsumer;
    }

    @Override
    public void publish(LogRecord record) {
        if (ApplicationManager.getApplication().isDisposed()) {
            Disposer.dispose(this);
            return;
        }

        super.publish(record);
    }

    @Override
    public void dispose() {
        disposableConsumer.run();
    }
}
