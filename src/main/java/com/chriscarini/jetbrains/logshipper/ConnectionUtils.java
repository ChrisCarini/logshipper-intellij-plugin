package com.chriscarini.jetbrains.logshipper;

import com.chriscarini.jetbrains.logshipper.configuration.SettingsManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.text.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.Socket;

public class ConnectionUtils {
    private static final Logger LOG = Logger.getInstance(ConnectionUtils.class);

    private ConnectionUtils() {
    }

    public static boolean isConnectable() {

        final SettingsManager.Settings settings = SettingsManager.getInstance().getState();

        final String hostname = settings.hostname;
        final String port = settings.port;

        if (StringUtil.isEmpty(hostname) || StringUtil.isEmpty(port)) {
            LOG.info(String.format("Logshipper hostname / port [%s:%s] is empty, please configure this in the settings.", hostname, port));
            return false;
        }

        if (!isConnectable(hostname, port)) {
            LOG.info(String.format("Logshipper hostname / port [%s:%s] is not connectable, please ensure the host+port is accessible.", hostname, port));
            return false;
        }

        return true;
    }


    public static boolean isConnectable(@NotNull final String hostname, @NotNull final String port) {
        try (final Socket sock = new Socket(hostname, Integer.parseInt(port))) {
            return sock.isConnected();
        } catch (IOException e) {
            return false;
        }
    }

}
