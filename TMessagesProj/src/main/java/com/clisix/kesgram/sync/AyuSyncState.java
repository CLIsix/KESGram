/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.clisix.kesgram.sync;

import android.util.Log;
import com.clisix.kesgram.AyuConstants;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;

public class AyuSyncState {
    private static int lastSent;
    private static int lastReceived;
    private static AyuSyncConnectionState connectionState = AyuSyncConnectionState.NotRegistered;
    private static int registerStatusCode;

    public static int getLastSent() {
        return lastSent;
    }

    public static void setLastSent(int lastSent) {
        AyuSyncState.lastSent = lastSent;

        AndroidUtilities.runOnUIThread(() -> NotificationCenter.getGlobalInstance().postNotificationName(AyuConstants.AYUSYNC_LAST_SENT_CHANGED));
    }

    public static int getLastReceived() {
        return lastReceived;
    }

    public static void setLastReceived(int lastReceived) {
        AyuSyncState.lastReceived = lastReceived;

        AndroidUtilities.runOnUIThread(() -> NotificationCenter.getGlobalInstance().postNotificationName(AyuConstants.AYUSYNC_LAST_RECEIVED_CHANGED));
    }

    public static AyuSyncConnectionState getConnectionState() {
        return connectionState;
    }

    public static void setConnectionState(AyuSyncConnectionState connectionState) {
        Log.d("AyuSync", "setConnectionState: " + connectionState);
        AyuSyncState.connectionState = connectionState;

        AndroidUtilities.runOnUIThread(() -> NotificationCenter.getGlobalInstance().postNotificationName(AyuConstants.AYUSYNC_STATE_CHANGED));
    }

    public static String getConnectionStateString() {
        String status;
        switch (getConnectionState()) {
            case Connected:
                status = LocaleController.getString(R.string.AyuSyncStatusOk);
                break;
            case Disconnected:
                status = LocaleController.getString(R.string.AyuSyncStatusErrorDisconnected);
                break;
            case NotRegistered:
                status = LocaleController.getString(R.string.AyuSyncStatusErrorNotRegistered);
                break;
            case NoToken:
                status = LocaleController.getString(R.string.AyuSyncStatusErrorNoToken);
                break;
            case InvalidToken:
                status = LocaleController.getString(R.string.AyuSyncStatusErrorInvalidToken);
                break;
            case NoMVP:
                status = LocaleController.getString(R.string.AyuSyncStatusErrorNoMVP);
                break;
            default:
                status = "unknown";
                break;
        }

        return status;
    }

    public static int getRegisterStatusCode() {
        return registerStatusCode;
    }

    public static void setRegisterStatusCode(int registerStatusCode) {
        AyuSyncState.registerStatusCode = registerStatusCode;

        AndroidUtilities.runOnUIThread(() -> NotificationCenter.getGlobalInstance().postNotificationName(AyuConstants.AYUSYNC_REGISTER_STATUS_CODE_CHANGED));
    }
}
