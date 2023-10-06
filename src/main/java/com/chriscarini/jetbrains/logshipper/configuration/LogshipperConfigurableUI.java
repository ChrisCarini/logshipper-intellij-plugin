package com.chriscarini.jetbrains.logshipper.configuration;

import com.chriscarini.jetbrains.logshipper.ConstantLogEntryTesterService;
import com.chriscarini.jetbrains.logshipper.LogstashLayoutAppenderService;
import com.chriscarini.jetbrains.logshipper.messages.Messages;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.options.ConfigurableUi;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.ui.TitledSeparator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.datatransfer.StringSelection;


/**
 * Generate the UI for Logshipper settings.
 */
public class LogshipperConfigurableUI implements ConfigurableUi<SettingsManager.Settings> {
    private static final Logger LOG = Logger.getInstance(LogshipperConfigurableUI.class);
    private static final String EXAMPLE_LOGSTASH_CONFIG =
            "input {\n" + "  tcp {\n" + "    port => 5000\n" + "    codec => json\n" + "  }\n" + "}\n" + "output {\n"
                    + "  elasticsearch {\n" + "    hosts => [\"127.0.0.1:9200\"]\n" + "    index => \"logshipper-demo\"\n"
                    + "  }\n" + "  stdout { codec => rubydebug }\n" + "}";
    @SuppressWarnings("rawtypes")
    private final JPanel mainPanel = new JBPanel();
    private final JBTextField hostnameField = new JBTextField();
    private final JBTextField portField = new JBTextField();
    private final JBCheckBox includeLocationInformationField = new JBCheckBox();
    private final JBCheckBox generateSampleLogMessagesField = new JBCheckBox();

    public LogshipperConfigurableUI() {
        buildMainPanel();
    }

    private void buildMainPanel() {
        LOG.debug("Creating main panel for Logshipper configuration...");
        mainPanel.setLayout(new VerticalFlowLayout(true, false));

        final JTextArea exampleConfigTextArea = new JTextArea(EXAMPLE_LOGSTASH_CONFIG);
        exampleConfigTextArea.setEnabled(true);
        exampleConfigTextArea.setEditable(false);
        final JButton copyExampleConfigToClipboardButton = new JButton(Messages.message("button.copy.to.clipboard"));
        copyExampleConfigToClipboardButton.addActionListener(e -> CopyPasteManager.getInstance().setContents(new StringSelection(EXAMPLE_LOGSTASH_CONFIG)));

        mainPanel.add(FormBuilder.createFormBuilder()
                .addComponent(new TitledSeparator(Messages.message("title.logstash.settings")))
                .addComponent(new JBLabel(Messages.message("title.logstash.settings.description")))
                .addLabeledComponent(Messages.message("title.logstash.settings.hostname"), hostnameField)
                .addLabeledComponent(Messages.message("title.logstash.settings.port"), portField)
                .addLabeledComponent(Messages.message("title.logstash.settings.include.log.location.info"),
                        includeLocationInformationField)
                .addTooltip(Messages.message("title.logstash.settings.tooltip"))
                .addComponent(new TitledSeparator(Messages.message("title.helpful.debug.settings")))
                .addLabeledComponent(Messages.message("title.helpful.debug.settings.generate.sample.log.messages"),
                        generateSampleLogMessagesField)
                .addTooltip(Messages.message("title.helpful.debug.settings.generate.sample.log.message.tooltip"))
                .addComponent(new TitledSeparator(Messages.message("title.sample.logstash.configuration")))
                .addComponent(new JBLabel(Messages.message("title.sample.logstash.configuration.description")))
                .addComponent(exampleConfigTextArea)
                .addComponent(copyExampleConfigToClipboardButton)
                .getPanel());
    }

    @Override
    public void reset(@NotNull final SettingsManager.Settings settings) {
        LOG.debug("Resetting Logshipper settings...");

        hostnameField.setText(settings.hostname);
        portField.setText(settings.port);
        includeLocationInformationField.setSelected(settings.includeLocationInformation);
        generateSampleLogMessagesField.setSelected(settings.generateSampleLogMessages);
    }

    @Override
    public boolean isModified(@NotNull final SettingsManager.Settings settings) {
        final boolean sameHostname = hostnameField.getText().equals(settings.hostname);
        final boolean samePort = portField.getText().equals(settings.port);
        final boolean sameAddConsoleAppender =
                Boolean.compare(includeLocationInformationField.isSelected(), settings.includeLocationInformation) == 0;
        final boolean sameGenerateSampleLogMessages =
                Boolean.compare(generateSampleLogMessagesField.isSelected(), settings.generateSampleLogMessages) == 0;
        return !sameHostname || !samePort || !sameAddConsoleAppender || !sameGenerateSampleLogMessages;
    }

    @Override
    public void apply(@NotNull final SettingsManager.Settings settings) {
        LOG.debug("Applying Logshipper settings...");

        final boolean sameGenerateSampleLogMessages =
                Boolean.compare(generateSampleLogMessagesField.isSelected(), settings.generateSampleLogMessages) == 0;
        if (!sameGenerateSampleLogMessages) {
            settings.generateSampleLogMessages = generateSampleLogMessagesField.isSelected();
            if (generateSampleLogMessagesField.isSelected()) {
                ConstantLogEntryTesterService.getInstance().initComponent();
            } else {
                ConstantLogEntryTesterService.getInstance().cancelLogMessageJob();
            }
        }

        settings.hostname = hostnameField.getText();
        settings.port = portField.getText();
        settings.includeLocationInformation = includeLocationInformationField.isSelected();

        // Re-init the LayoutAppender Service to pick up the latest settings.
        LogstashLayoutAppenderService.getInstance().init();
    }

    @NotNull
    @Override
    public JComponent getComponent() {
        return mainPanel;
    }
}
