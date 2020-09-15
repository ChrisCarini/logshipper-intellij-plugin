package com.chriscarini.jetbrains.logshipper.logger;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.spi.LoggingEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * A {@link org.apache.log4j.net.SocketAppender} that allows for a {@link org.apache.log4j.Layout} to be used when
 * writing out to the host/port.
 *
 * We *CAN NOT* just use {@link org.apache.log4j.net.SocketAppender} or extend it to work with a
 * {@link org.apache.log4j.Layout}. The issue is when trying to use the append() method to send an event, the `message`
 * field of the {@link LoggingEvent} can not be set to anything that will produce json on the respective socket. I've
 * tried:
 *    1) passing a raw json {@link String}
 *    2) passing a {@link net.minidev.json.JSONObject}
 *
 * No success in any of these. Boo. :(
 *
 * This should functionally be identical to {@link org.apache.log4j.net.SocketAppender}, with the only difference
 * being that we use an {@link OutputStreamWriter} in place of an {@link java.io.ObjectOutputStream} to allow us
 * to send a {@link org.apache.log4j.Layout} formatted string over a {@link Socket}.
 */
public class LayoutSocketAppender extends AppenderSkeleton {
  public static final int DEFAULT_RECONNECT_DELAY = 30000;
  private final InetAddress address;
  private final int port;
  private final int reconnectionDelay;
  private OutputStreamWriter osw;
  private int counter = 0;
  private Connector connector;

  /**
   * Default constructor
   *
   * @param host              The hostname to connect to
   * @param port              The port to connect on
   * @param reconnectionDelay The delay (in ms) to wait before reconnecting
   */
  public LayoutSocketAppender(final String host, final int port, final int reconnectionDelay) {
    this.port = port;
    this.address = getAddressByName(host);
    this.reconnectionDelay = reconnectionDelay;
    this.connect(this.address, this.port);
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

  public void cleanUp() {
    if (this.osw != null) {
      try {
        this.osw.close();
      } catch (IOException var2) {
        if (var2 instanceof InterruptedIOException) {
          Thread.currentThread().interrupt();
        }

        LogLog.error("Could not close oos.", var2);
      }

      this.osw = null;
    }

    if (this.connector != null) {
      this.connector.interrupted = true;
      this.connector = null;
    }
  }

  void connect(InetAddress address, int port) {
    if (this.address != null) {
      try {
        this.cleanUp();
        this.osw = new OutputStreamWriter((new Socket(address, port)).getOutputStream(), StandardCharsets.UTF_8);
      } catch (final IOException e) {
        if (e instanceof InterruptedIOException) {
          Thread.currentThread().interrupt();
        }

        String msg = "Could not connect to remote log4j server at [" + address.getHostName() + "].";
        if (this.reconnectionDelay > 0) {
          msg = msg + " We will try again later.";
          this.fireConnector();
        } else {
          msg = msg + " We are not retrying.";
          this.errorHandler.error(msg, e, 0);
        }
        LogLog.error(msg);
      }
    }
  }

  /**
   * Append a {@link LoggingEvent} to the {@link LayoutSocketAppender}.
   *
   * @param event The {@link LoggingEvent} to append and send out the {@link LayoutSocketAppender}.
   */
  @Override
  public void append(@Nullable final LoggingEvent event) {
    if (event == null) {
      return;
    }
    if (this.address == null) {
      this.errorHandler.error("No remote host is set for LogstashJSONSocketAppender named \"" + this.name + "\".");
      return;
    }
    if (this.osw == null) {
      return;
    }
    if (this.layout == null) {
      this.errorHandler.error("No layout is set for LogstashJSONSocketAppender named \"" + this.name + "\".");
      return;
    }
    try {
      final String format = this.layout.format(event);
      this.osw.write(format);
      this.osw.flush();
      if (++this.counter >= 1) {
        this.counter = 0;
      }
    } catch (IOException exception) {
      if (exception instanceof InterruptedIOException) {
        Thread.currentThread().interrupt();
      }

      this.osw = null;
      LogLog.warn("Detected problem with connection: " + exception);
      if (this.reconnectionDelay > 0) {
        this.fireConnector();
      } else {
        this.errorHandler.error("Detected problem with connection, not reconnecting.", exception, 0);
      }
    }
  }

  private void fireConnector() {
    if (this.connector == null) {
      LogLog.debug("Starting a new connector thread.");
      this.connector = new LayoutSocketAppender.Connector();
      this.connector.setDaemon(true);
      this.connector.setPriority(1);
      this.connector.start();
    }
  }

  /**
   * Does this {@link AppenderSkeleton} require a {@link org.apache.log4j.Layout}? Yes, yes it does.
   *
   * @return Always {@code True}, as we require a {@link org.apache.log4j.Layout}.
   */
  @Override
  public boolean requiresLayout() {
    return true;
  }

  class Connector extends Thread {
    boolean interrupted = false;

    Connector() {
    }

    public void run() {
      while (true) {
        if (!this.interrupted) {
          try {
            sleep(LayoutSocketAppender.this.reconnectionDelay);
            LogLog.debug("Attempting connection to " + LayoutSocketAppender.this.address.getHostName() + ":"
                + LayoutSocketAppender.this.port);
            synchronized (this) {
              LayoutSocketAppender.this.osw = new OutputStreamWriter(
                  (new Socket(LayoutSocketAppender.this.address, LayoutSocketAppender.this.port)).getOutputStream(),
                  StandardCharsets.UTF_8);
              LayoutSocketAppender.this.connector = null;
              LogLog.debug("Connection established. Exiting connector thread.");
            }
          } catch (final InterruptedException e) {
            LogLog.debug("Connector interrupted. Leaving loop.");
            return;
          } catch (final ConnectException e) {
            LogLog.debug(
                "Remote host " + LayoutSocketAppender.this.address.getHostName() + ":" + LayoutSocketAppender.this.port
                    + " refused connection.");
            continue;
          } catch (final IOException e) {
            if (e instanceof InterruptedIOException) {
              Thread.currentThread().interrupt();
            }

            LogLog.debug("Could not connect to " + LayoutSocketAppender.this.address.getHostName() + ":"
                + LayoutSocketAppender.this.port + ". Exception is " + e);
            continue;
          }
        }
        return;
      }
    }
  }
}