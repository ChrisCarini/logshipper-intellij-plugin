package com.chriscarini.jetbrains.logshipper.configuration;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;


@State(name = "LogShipperSettings", storages = @Storage("LogShipperSettings.xml"))
public class SettingsManager implements PersistentStateComponent<SettingsManager.Settings> {
    private Settings state;

    public SettingsManager() {
        this.state = new Settings();
    }

    public static SettingsManager getInstance() {
        return ApplicationManager.getApplication().getService(SettingsManager.class);
    }

    @NotNull
    @Override
    public Settings getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull final Settings state) {
        this.state = state;
    }

    public static class Settings {
        public String hostname;
        public String port;
        public boolean includeLocationInformation;
        public boolean generateSampleLogMessages;
    }
}
