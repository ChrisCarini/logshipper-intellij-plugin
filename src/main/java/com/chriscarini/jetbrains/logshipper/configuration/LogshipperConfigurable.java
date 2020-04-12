package com.chriscarini.jetbrains.logshipper.configuration;

import com.chriscarini.jetbrains.logshipper.messages.Messages;
import com.intellij.openapi.options.ConfigurableBase;
import org.jetbrains.annotations.NotNull;


/**
 * Log Shipper configuration that represents server settings
 */
public class LogshipperConfigurable extends ConfigurableBase<LogshipperConfigurableUI, SettingsManager.Settings> {
  protected LogshipperConfigurable() {
    super("logshipper.settings", Messages.message("configuration.window.display.name.logshipper.settings"), null);
  }

  @NotNull
  @Override
  protected SettingsManager.Settings getSettings() {
    return SettingsManager.getInstance().getState();
  }

  @Override
  protected LogshipperConfigurableUI createUi() {
    return new LogshipperConfigurableUI();
  }
}