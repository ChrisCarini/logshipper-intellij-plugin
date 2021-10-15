package com.chriscarini.jetbrains.logshipper.logger;

import com.intellij.ide.IdeBundle;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PermanentInstallationID;
import com.intellij.openapi.application.ex.ApplicationInfoEx;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.LicensingFacade;
import com.intellij.util.text.DateFormatUtil;
import java.util.Properties;
import java.util.function.Consumer;
import net.logstash.log4j.JSONEventLayoutV1;
import org.apache.log4j.spi.LoggingEvent;
import org.jetbrains.annotations.Nullable;


/**
 * A {@link LayoutSocketAppender} for Logstash. Automatically registers the {@link JSONEventLayoutV1} for the layout.
 */
public class LogstashJSONSocketAppender extends LayoutSocketAppender implements Disposable {
  private final Consumer<LogstashJSONSocketAppender> disposableConsumer;

  public LogstashJSONSocketAppender(final String host, final int port, final int reconnectionDelay,
      final boolean includeLocationInfo, final Consumer<LogstashJSONSocketAppender> disposableConsumer) {
    super(host, port, reconnectionDelay);
    setLayout(new JSONEventLayoutV1(includeLocationInfo));
    this.disposableConsumer = disposableConsumer;
  }

  @Override
  public void append(@Nullable LoggingEvent event) {
    if (ApplicationManager.getApplication().isDisposed()) {
      Disposer.dispose(this);
    }

    setEventPropertyIfNotNull(event, "PermanentInstallationID", PermanentInstallationID.get());

    final Properties properties = System.getProperties();
    setEventPropertyIfNotNull(event, "JREInformation", IdeBundle.message("about.box.jre",
        properties.getProperty("java.runtime.version", properties.getProperty("java.version", "unknown")),
        properties.getProperty("os.arch", "")));
    setEventPropertyIfNotNull(event, "JVMInformation",
        IdeBundle.message("about.box.vm", properties.getProperty("java.vm.name", "unknown"),
            properties.getProperty("java.vendor", "unknown")));

    if (!ApplicationManager.getApplication().isDisposed()) {
      final ApplicationInfoEx appInfoEx = ApplicationInfoEx.getInstanceEx();
      setEventPropertyIfNotNull(event, "FullApplicationName", appInfoEx.getFullApplicationName());
      setEventPropertyIfNotNull(event, "ApiVersion", appInfoEx.getApiVersion());
      setEventPropertyIfNotNull(event, "CompanyName", appInfoEx.getCompanyName());
      setEventPropertyIfNotNull(event, "FullVersion", appInfoEx.getFullVersion());
      setEventPropertyIfNotNull(event, "StrictVersion", appInfoEx.getStrictVersion());
      setEventPropertyIfNotNull(event, "VersionName", appInfoEx.getVersionName());
      setEventPropertyIfNotNull(event, "Build", appInfoEx.getBuild().asString());
      setEventPropertyIfNotNull(event, "BuildDate",
          DateFormatUtil.getIso8601Format().format(appInfoEx.getBuildDate().getTime()));
    }

    final LicensingFacade licensingFacade = LicensingFacade.getInstance();
    if (licensingFacade != null) {
      setEventPropertyIfNotNull(event, "LicensedToMessage", licensingFacade.getLicensedToMessage());
      setEventPropertyIfNotNull(event, "LicenseRestrictionsMessages",
          String.join(";", licensingFacade.getLicenseRestrictionsMessages()));
      setEventPropertyIfNotNull(event, "isEvaluationLicense", Boolean.toString(licensingFacade.isEvaluationLicense()));
      setEventPropertyIfNotNull(event, "LicenseExpirationDate",
          licensingFacade.getLicenseExpirationDate() != null ? DateFormatUtil.getIso8601Format()
              .format(licensingFacade.getLicenseExpirationDate()) : "Unknown");
      setEventPropertyIfNotNull(event, "ConfirmationStamps", licensingFacade.confirmationStamps != null ? licensingFacade.confirmationStamps.toString() : "null");
    }

    // Do the normal thing now that we've augmented the event w/ data we care about.
    super.append(event);
  }

  private void setEventPropertyIfNotNull(@Nullable LoggingEvent event, String propName,
      @Nullable String propValue) {
    if (event != null) {
      event.setProperty(propName, propValue != null ? propValue : "null");
    }
  }

  @Override
  public void dispose() {
    disposableConsumer.accept(this);
  }
}