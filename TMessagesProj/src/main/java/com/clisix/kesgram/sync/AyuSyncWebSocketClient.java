/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.clisix.kesgram.sync;

import com.google.android.exoplayer2.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.clisix.kesgram.AyuConfig;
import com.clisix.kesgram.AyuUtils;
import dev.gustavoavila.websocketclient.WebSocketClient;
import dev.gustavoavila.websocketclient.exceptions.InvalidServerHandshakeException;

import java.net.URI;
import java.net.URISyntaxException;

public class AyuSyncWebSocketClient extends WebSocketClient {

    private static AyuSyncWebSocketClient instance;

    private AyuSyncWebSocketClient(URI uri) {
        super(uri);
    }

    public static boolean create() {
        if (instance != null) {
            return true;
        }

        URI url;
        try {
            url = new URI(AyuSyncConfig.getWebSocketURL());
        } catch (URISyntaxException e) {
            return false;
        }

        Log.d("AyuSync", "Creating new WebSocket client");

        instance = new AyuSyncWebSocketClient(url);

        instance.setConnectTimeout(5000);
        instance.setReadTimeout(60000);
        instance.addHeader("X-APP-PACKAGE", AyuUtils.getPackageName());
        instance.addHeader("X-DEVICE-IDENTIFIER", AyuUtils.getDeviceIdentifier());
        instance.addHeader("Authorization", AyuSyncConfig.getToken());
        instance.enableAutomaticReconnection(1500);
        instance.connect();

        return true;
    }

    public static AyuSyncWebSocketClient getInstance() {
        if (instance == null) {
            create();
        }

        return instance;
    }

    public static void nullifyInstance() {
        if (instance == null) {
            return;
        }

        AyuSyncState.setConnectionState(AyuSyncConnectionState.Disconnected);

        try {
            // crashed once with "java.lang.IllegalStateException: Timer already cancelled."
            instance.close(200, 0, "nullified");
        } catch (Exception e) {
            Log.e("AyuSync", "Error while closing WebSocket", e);
        }

        instance = null;
    }

    @Override
    public void send(String message) {
        try {
            super.send(message);

            AyuSyncState.setLastSent((int) (System.currentTimeMillis() / 1000));
        } catch (Exception e) {
            Log.e("AyuSync", "Error while sending message", e);
        }
    }

    @Override
    public void onOpen() {
        AyuSyncState.setConnectionState(AyuSyncConnectionState.Connected);

        Log.d("AyuSync", "Connected to the origin");
    }

    @Override
    public void onTextReceived(String message) {
        AyuSyncState.setLastReceived((int) (System.currentTimeMillis() / 1000));

        try {
            var response = new Gson().fromJson(message, JsonObject.class);
            AyuSyncController.getInstance().invokeHandler(response);
        } catch (Exception e) {
            Log.e("AyuSync", "Error while invoking handler", e);
        }
    }

    @Override
    public void onBinaryReceived(byte[] data) {
        Log.d("AyuSync", "binary received");
    }

    @Override
    public void onPingReceived(byte[] data) {
//        Log.d("AyuSync", "ping!");
    }

    @Override
    public void onPongReceived(byte[] data) {
//        Log.d("AyuSync", "pong!");
    }

    @Override
    public void onException(Exception e) {
        AyuSyncState.setConnectionState(AyuSyncConnectionState.Disconnected);

        Log.e("AyuSync", e.toString());

        if ((e instanceof InvalidServerHandshakeException) && AyuConfig.syncEnabled && instance == this) {
            // this fucking library doesn't support any other exception except `IOException`
            // so we have to reinitialize instance

            // using reflection call doesn't work

            AyuSyncController.nullifyInstance();

            try {
                Thread.sleep(1500);
            } catch (Exception e2) {
                Log.d("AyuSync", "jaBBa", e2);
            }

            AyuSyncController.create();
        }
    }

    @Override
    public void onCloseReceived(int reason, String description) {
        AyuSyncState.setConnectionState(AyuSyncConnectionState.Disconnected);

        Log.d("AyuSync", "Disconnected from the origin: " + description);
    }
}
