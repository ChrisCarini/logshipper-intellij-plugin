package com.chriscarini.jetbrains.logshipper.logger;

import net.logstash.log4j.JSONEventLayoutV1;


/**
 * A {@link LayoutSocketAppender} for Logstash. Automatically registers the {@link JSONEventLayoutV1} for the layout.
 */
public class LogstashJSONSocketAppender extends LayoutSocketAppender {
  public LogstashJSONSocketAppender(final String host, final int port, final int reconnectionDelay,
      final boolean includeLocationInfo) {
    super(host, port, reconnectionDelay);
    setLayout(new JSONEventLayoutV1(includeLocationInfo));
  }
}