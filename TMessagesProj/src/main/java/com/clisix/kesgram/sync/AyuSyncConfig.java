/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.clisix.kesgram.sync;

import com.clisix.kesgram.AyuConfig;

public class AyuSyncConfig {
    private static String getWebSocketProtocol() {
        return AyuConfig.useSecureConnection ? "wss://" : "ws://";
    }

    private static String getHTTPProtocol() {
        return AyuConfig.useSecureConnection ? "https://" : "http://";
    }

    public static String getWebSocketURL() {
        return getWebSocketProtocol() + AyuConfig.getSyncServerURL() + "/sync/ws/v1";
    }

    public static String getUserDataURL() {
        return getHTTPProtocol() + AyuConfig.getSyncServerURL() + "/user/v1";
    }

    public static String getRegisterDeviceURL() {
        return getHTTPProtocol() + AyuConfig.getSyncServerURL() + "/sync/register/v1";
    }

    public static String getForceSyncURL() {
        return getHTTPProtocol() + AyuConfig.getSyncServerURL() + "/sync/force/v1";
    }

    public static String getToken() {
        return AyuConfig.getSyncServerToken();
    }

    public static String getProfileURL() {
        return getHTTPProtocol() + AyuConfig.getSyncServerURL() + "/ui/profile?token=" + AyuConfig.getSyncServerToken();
    }
}
