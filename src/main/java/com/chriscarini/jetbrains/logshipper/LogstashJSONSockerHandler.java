package com.chriscarini.jetbrains.logshipper;

import com.chriscarini.jetbrains.logshipper.logger.LogstashUtilFormatter;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Disposer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.LogRecord;
import java.util.logging.SocketHandler;

public class LogstashJSONSockerHandler extends SocketHandler implements Disposable {
    private final Runnable disposableConsumer;
    // TODO(ChrisCarini) - Possible way to avoid the `@SuppressWarnings("this-escape")` annotation.
//    private final boolean includeLocationInfo;

    @SuppressWarnings("this-escape")
    public LogstashJSONSockerHandler(final String host, final int port, final boolean includeLocationInfo, @NotNull final Runnable disposableConsumer) throws IOException {
        super(host, port);
        setFormatter(new LogstashUtilFormatter(includeLocationInfo));
        // TODO(ChrisCarini) - Possible way to avoid the `@SuppressWarnings("this-escape")` annotation.
//        this.includeLocationInfo = includeLocationInfo;
        this.disposableConsumer = disposableConsumer;
    }

    @Override
    public void publish(LogRecord record) {
        if (ApplicationManager.getApplication().isDisposed()) {
            Disposer.dispose(this);
        }

        // TODO(ChrisCarini) - Possible way to avoid the `@SuppressWarnings("this-escape")` annotation.
//        if(!(this.getFormatter() instanceof LogstashUtilFormatter)){
//            this.setFormatter(new LogstashUtilFormatter(this.includeLocationInfo));
//        }

        super.publish(record);
    }

    @Override
    public void dispose() {
        disposableConsumer.run();
    }
}
