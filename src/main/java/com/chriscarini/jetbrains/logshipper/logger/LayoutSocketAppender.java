package com.chriscarini.jetbrains.logshipper.logger;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * A {@link org.apache.log4j.net.SocketAppender} that allows for a {@link org.apache.log4j.Layout} to be used when
 * writing out to the host/port.
 */
public class LayoutSocketAppender extends AppenderSkeleton {
    public static final int DEFAULT_RECONNECT_DELAY = 30000;
    private final InetAddress address;
    private final int port;
    private final int reconnectionDelay;
    private PrintWriter printWriter;
    private int counter = 0;
    private Connector connector;

    /**
     * Default constructor
     * @param host The hostname to connect to
     * @param port The port to connect on
     * @param reconnectionDelay The delay (in ms) to wait before reconnecting
     */
    public LayoutSocketAppender(final String host, final int port, final int reconnectionDelay) {
        this.port = port;
        this.address = getAddressByName(host);
        this.reconnectionDelay = reconnectionDelay;
        this.connect();
    }

    @Nullable
    private static InetAddress getAddressByName(@NotNull final String host) {
        try {
            return InetAddress.getByName(host);
        } catch (final UnknownHostException e) {
            LogLog.error("Could not find address of [" + host + "].", e);
            return null;
        }
    }

    /**
     * Close the {@link LayoutSocketAppender}.
     */
    @Override
    public synchronized void close() {
        if (!this.closed) {
            this.closed = true;
            this.cleanUp();
        }
    }

    private void cleanUp() {
        if (this.printWriter != null) {
            this.printWriter.close();
            this.printWriter = null;
        }

        if (this.connector != null) {
            this.connector.interrupted = true;
            this.connector = null;
        }
    }

    private void connect() {
        if (this.address != null) {
            try {
                this.cleanUp();
                this.printWriter = new PrintWriter((new Socket(this.address, this.port)).getOutputStream());
            } catch (final IOException e) {
                if (e instanceof InterruptedIOException) {
                    Thread.currentThread().interrupt();
                }

                final StringBuilder sb = new StringBuilder(
                        "Could not connect to remote log4j server at [" + this.address.getHostName() + "].");
                if (this.reconnectionDelay > 0) {
                    sb.append(" We will try again later.");
                    this.fireConnector();
                } else {
                    sb.append(" We are not retrying.");
                    this.errorHandler.error(sb.toString(), e, 0);
                }
                LogLog.error(sb.toString());
            }
        }
    }

    /**
     * Append a {@link LoggingEvent} to the {@link LayoutSocketAppender}.
     * @param event The {@link LoggingEvent} to append and send out the {@link LayoutSocketAppender}.
     */
    @Override
    public void append(@Nullable final LoggingEvent event) {
        if (event == null) {
            return;
        }
        if (this.address == null) {
            this.errorHandler.error(
                    "No remote host is set for LogstashJSONSocketAppender named \"" + this.name + "\".");
            return;
        }
        if (this.printWriter == null) {
            return;
        }
        if (this.layout == null) {
            this.errorHandler.error("No layout is set for LogstashJSONSocketAppender named \"" + this.name + "\".");
            return;
        }
        final String format = this.layout.format(event);
        this.printWriter.append(format);
        this.printWriter.flush();
        if (++this.counter >= 1) {
            this.counter = 0;
        }
    }

    private void fireConnector() {
        if (this.connector == null) {
            LogLog.debug("Starting a new connector thread.");
            this.connector = new Connector(this.address, this.port);
            this.connector.setDaemon(true);
            this.connector.setPriority(1);
            this.connector.start();
        }
    }

    /**
     * Does this {@link AppenderSkeleton} require a {@link org.apache.log4j.Layout}? Yes, yes it does.
     * @return Always {@code True}, as we require a {@link org.apache.log4j.Layout}.
     */
    @Override
    public boolean requiresLayout() {
        return true;
    }

    private class Connector extends Thread {
        private final InetAddress address;
        private final int port;
        boolean interrupted = false;

        Connector(final InetAddress address, final int port) {
            this.address = address;
            this.port = port;
        }

        public void run() {
            while (true) {
                if (!this.interrupted) {
                    try {
                        sleep((long) LayoutSocketAppender.this.reconnectionDelay);
                        LogLog.debug("Attempting connection to " + this.address.getHostName());
                        synchronized (this) {
                            LayoutSocketAppender.this.printWriter =
                                    new PrintWriter((new Socket(this.address, this.port)).getOutputStream());
                            LayoutSocketAppender.this.connector = null;
                            LogLog.debug("Connection established. Exiting connector thread.");
                        }
                    } catch (final InterruptedException e) {
                        LogLog.debug("Connector interrupted. Leaving loop.");
                        return;
                    } catch (final ConnectException e) {
                        LogLog.debug("Remote host " + this.address.getHostName() + " refused connection.");
                        continue;
                    } catch (final IOException e) {
                        if (e instanceof InterruptedIOException) {
                            Thread.currentThread().interrupt();
                        }

                        LogLog.debug("Could not connect to " + this.address.getHostName() + ". Exception is " + e);
                        continue;
                    }
                }
                return;
            }
        }
    }
}