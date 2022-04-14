package com.chriscarini.jetbrains.logshipper.logger.data;


import java.net.UnknownHostException;

public class HostData {

    public HostData() {
    }

    public static String getHostName() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown-host";
        }
    }
}