package com.chriscarini.jetbrains.logshipper.logger;


import com.chriscarini.jetbrains.logshipper.logger.data.HostData;
import com.intellij.ide.IdeBundle;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.PermanentInstallationID;
import com.intellij.openapi.application.ex.ApplicationInfoEx;
import com.intellij.ui.LicensingFacade;
import java.time.ZoneOffset;
import java.util.Date;
import org.apache.commons.lang3.time.FastDateFormat;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObjectBuilder;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 *
 */
public class LogstashUtilFormatter extends Formatter {
    private boolean locationInfo;

    private static final JsonBuilderFactory BUILDER = Json.createBuilderFactory(null);

    private static final String hostname = HostData.getHostName();
    private static final Integer VERSION = 1;

    // TODO(ChrisCarini) - Change this property key.
    private static final String[] tags = System.getProperty(LogstashUtilFormatter.class.getCanonicalName() + ".tags", "UNKNOWN").split(",");

    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    public static final FastDateFormat ISO_DATETIME_TIME_ZONE_FORMAT_WITH_MILLIS = FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", UTC);


    public LogstashUtilFormatter() {
        this(true);
    }

    public LogstashUtilFormatter(final boolean locationInfo) {
        this.locationInfo = locationInfo;
    }

    @Override
    public final String format(final LogRecord record) {
        return BUILDER.createObjectBuilder()
                .add("@version", VERSION)
                .add("@timestamp", dateFormat(record.getMillis()))

                // Forward-looking fields; similar to the format in https://github.com/SYNAXON/logstash-util-formatter
                .add("@message", record.getMessage())
                .add("@source", record.getLoggerName())
                .add("@source_host", hostname)
                .add("@fields", encodeFields(record))
                .add("@tags", buildTags())

                // Add entries for backwards compatibility with `JSONEventLayoutV1` from pre-2022.
                // Slated for removal in a future release.
                .addAll(BackwardsCompatibleEntriesBuilder.buildBackwardsCompatibleEntries(record, locationInfo))
                .build().toString() + "\n";
    }

    @NotNull
    private static String dateFormat(final long timestamp) {
        return ISO_DATETIME_TIME_ZONE_FORMAT_WITH_MILLIS.format(timestamp);
    }

    /**
     * Encode all additional fields.
     *
     * @param record the log record
     * @return objectBuilder
     */
    @NotNull
    private JsonObjectBuilder encodeFields(@NotNull final LogRecord record) {
        final JsonObjectBuilder builder = BUILDER.createObjectBuilder()
                .add("timestamp", record.getMillis())
                .add("level", record.getLevel().toString())
                .add("thread_id", record.getLongThreadID())
                .add("thread_name", Thread.currentThread().getName())
                .add("class", Objects.requireNonNullElse(record.getSourceClassName(), "null"))
                .add("method", Objects.requireNonNullElse(record.getSourceMethodName(), "null"))
                .add("sequence_number", record.getSequenceNumber());

        // Populate any fields needed for the throwable
        final Throwable thrown = record.getThrown();
        if (thrown != null) {
            final JsonObjectBuilder exceptionBuilder = BUILDER.createObjectBuilder();
            exceptionBuilder.add("exception_class", thrown.getClass().getName());  // 2022.1 - New Addition!
            exceptionBuilder.add("exception_canonical_class", thrown.getClass().getCanonicalName());
            if (thrown.getMessage() != null) {
                exceptionBuilder.add("exception_message", thrown.getMessage());
            }
            exceptionBuilder.add("stacktrace", getStacktrace(thrown));

            if (this.locationInfo) {
                // Note: The below adds the above 4 fields (file, line_number, class, method) into the exception structure.
                exceptionBuilder.addAll(BackwardsCompatibleEntriesBuilder.buildLocationInfo(thrown));
            }

            builder.add("exception", exceptionBuilder);
        }

        // Populate any IntelliJ / JetBrains fields
        builder.add("jetbrains", BackwardsCompatibleEntriesBuilder.buildMDC());

        return builder;
    }

    @NotNull
    private static String getStacktrace(@NotNull final Throwable thrown) {
        final StringWriter sw = new StringWriter();
        thrown.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

    @NotNull
    private JsonArrayBuilder buildTags() {
        final JsonArrayBuilder tagsBuilder = BUILDER.createArrayBuilder();
        Arrays.stream(tags).forEach(tagsBuilder::add);
        return tagsBuilder;
    }

    private static class BackwardsCompatibleEntriesBuilder {

        /**
         * TODO(ChrisCarini) Removal Candidate: All fields added here - with the exception of
         *  the `mdc` field, which is specific to Log4J - exist under `exception`.
         */
        static JsonObjectBuilder buildBackwardsCompatibleEntries(@NotNull final LogRecord record, final boolean locationInfo) {
            final JsonObjectBuilder backwardsCompatibleEntriesBuilder = BUILDER.createObjectBuilder()
                    .add("source_host", hostname)                          // TODO - Removal Candidate: Exists as `@source_host`
                    .add("message", record.getMessage())                   // TODO - Removal Candidate: Exists as `@message`
                    .add("logger_name", record.getLoggerName())            // TODO - Removal Candidate: Exists as `@source`
                    .add("mdc", BackwardsCompatibleEntriesBuilder.buildMDC())
                    .add("level", record.getLevel().toString())            // TODO - Removal Candidate: Exists as `@fields.level`
                    .add("thread_name", Thread.currentThread().getName()); // TODO - Removal Candidate: Exists as `@fields.thread_name`

            if (record.getThrown() != null) {
                backwardsCompatibleEntriesBuilder.add("exception", BackwardsCompatibleEntriesBuilder.buildExceptionInformation(record)); // For backwards compatibility with `JSONEventLayoutV1` from pre-2022.
            }

            if (locationInfo) {
                backwardsCompatibleEntriesBuilder.addAll(BackwardsCompatibleEntriesBuilder.buildLocationInfo(record.getThrown())); // For backwards compatibility with `JSONEventLayoutV1` from pre-2022.
            }

            return backwardsCompatibleEntriesBuilder;
        }

        /**
         * TODO(ChrisCarini) Removal Candidate: All fields added here exist under `@fields.jetbrains`.
         *  This method is still useful, and should be migrated out of `BackwardsCompatibleEntriesBuilder`
         *  for re-use in the post-2022.1 fields.
         */
        private static JsonObjectBuilder buildMDC() {
            final Properties properties = System.getProperties();
            final JsonObjectBuilder mdcBuilder = BUILDER.createObjectBuilder();

            mdcBuilder.add("PermanentInstallationID", PermanentInstallationID.get())
                    .add("JREInformation", IdeBundle.message("about.box.jre", properties.getProperty("java.runtime.version", properties.getProperty("java.version", "unknown")), properties.getProperty("os.arch", "")))
                    .add("JVMInformation", IdeBundle.message("about.box.vm", properties.getProperty("java.vm.name", "unknown"), properties.getProperty("java.vendor", "unknown")));

            if (!ApplicationManager.getApplication().isDisposed()) {
                final ApplicationInfoEx appInfoEx = ApplicationInfoEx.getInstanceEx();
                mdcBuilder.add("FullApplicationName", appInfoEx.getFullApplicationName());
                mdcBuilder.add("ApiVersion", appInfoEx.getApiVersion());
                mdcBuilder.add("CompanyName", appInfoEx.getCompanyName());
                mdcBuilder.add("FullVersion", appInfoEx.getFullVersion());
                mdcBuilder.add("StrictVersion", appInfoEx.getStrictVersion());
                mdcBuilder.add("VersionName", appInfoEx.getVersionName());
                mdcBuilder.add("Build", appInfoEx.getBuild().asString());
                mdcBuilder.add("BuildDate", formatDateUTC(appInfoEx.getBuildDate().getTime()));
            }

            final LicensingFacade licensingFacade = LicensingFacade.getInstance();
            if (licensingFacade != null) {
                mdcBuilder.add("LicensedToMessage", Objects.requireNonNullElse(licensingFacade.getLicensedToMessage(), ""));
                mdcBuilder.add("LicenseRestrictionsMessages", String.join(";", licensingFacade.getLicenseRestrictionsMessages()));
                mdcBuilder.add("isEvaluationLicense", Boolean.toString(licensingFacade.isEvaluationLicense()));
                mdcBuilder.add("LicenseExpirationDate", licensingFacade.getLicenseExpirationDate() != null ? formatDateUTC(licensingFacade.getLicenseExpirationDate()) : "Unknown");
                mdcBuilder.add("ConfirmationStamps", licensingFacade.confirmationStamps != null ? licensingFacade.confirmationStamps.toString() : "null");
            }
            return mdcBuilder;
        }
        
        private static String formatDateUTC(final Date date) {
            return DateTimeFormatter.ISO_DATE_TIME.format(date.toInstant().atZone(ZoneOffset.UTC));
        }

        /**
         * TODO(ChrisCarini) Removal Candidate: All fields added here exist under `exception`.
         */
        private static JsonObjectBuilder buildExceptionInformation(LogRecord record) {
            final JsonObjectBuilder exceptionInformationBuilder = BUILDER.createObjectBuilder();

            final Throwable thrown = record.getThrown();
            if (thrown.getClass().getCanonicalName() != null) {
                exceptionInformationBuilder.add("exception_class", thrown.getClass().getCanonicalName()); // TODO - Removal Candidate: Exists as `@fields.exception.exception_canonical_class`
            }
            if (thrown.getMessage() != null) {
                exceptionInformationBuilder.add("exception_message", thrown.getMessage());                // TODO - Removal Candidate: Exists as `@fields.exception.exception_message`
            }
            exceptionInformationBuilder.add("stacktrace", getStacktrace(thrown));                                    // TODO - Removal Candidate: Exists as `@fields.exception.stacktrace`

            return exceptionInformationBuilder;
        }

        /**
         * TODO(ChrisCarini) Removal Candidate: This method is still useful,
         *  and should be migrated out of `BackwardsCompatibleEntriesBuilder`
         *  for re-use in the post-2022.1 fields.
         */
        @NotNull
        private static JsonObjectBuilder buildLocationInfo(@Nullable final Throwable throwable) {
            final JsonObjectBuilder locationInfoBuilder = BUILDER.createObjectBuilder();

            if (throwable == null) {
                return locationInfoBuilder;
            }

            final StackTraceElement stackTraceElement = Arrays.stream(throwable.getStackTrace())
                    .findFirst()
                    .orElse(null);

            if (stackTraceElement != null) {
                locationInfoBuilder.add("file", stackTraceElement.getFileName());           // TODO - Removal Candidate: Exists as `@fields.exception.file`
                locationInfoBuilder.add("line_number", stackTraceElement.getLineNumber());  // TODO - Removal Candidate: Exists as `@fields.exception.line_number`
                locationInfoBuilder.add("class", stackTraceElement.getClassName());         // TODO - Removal Candidate: Exists as `@fields.exception.class`
                locationInfoBuilder.add("method", stackTraceElement.getMethodName());       // TODO - Removal Candidate: Exists as `@fields.exception.method`
            }

            return locationInfoBuilder;
        }
    }

    @Override
    public synchronized String formatMessage(final LogRecord record) {
        String message = super.formatMessage(record);

        try {
            final Object[] parameters = record.getParameters();
            if (Objects.equals(message, record.getMessage()) && parameters != null && parameters.length > 0) {
                message = String.format(message, parameters);
            }
        } catch (Exception ignored) {
        }

        return message;
    }
}