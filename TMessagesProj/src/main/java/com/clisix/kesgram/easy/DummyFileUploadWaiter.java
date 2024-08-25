/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.clisix.kesgram.easy;

import android.text.TextUtils;
import android.util.Log;
import org.telegram.messenger.NotificationCenter;

public class DummyFileUploadWaiter extends EasyWaiter {
    private final String path;

    public DummyFileUploadWaiter(int currentAccount, String path) {
        super(currentAccount);
        this.path = path;

        notifications.add(NotificationCenter.fileUploaded);
        notifications.add(NotificationCenter.fileUploadFailed);
    }

    private void process(String path) {
        if (this.path.contains(path) || (!TextUtils.isEmpty(path) && path.endsWith(this.path))) {
            unsubscribe();
        }
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.fileUploaded) {
            final String location = (String) args[0];

            Log.w("AyuGram", "File uploaded: " + location);

            process(location);
        } else if (id == NotificationCenter.fileUploadFailed) {
            final String location = (String) args[0];

            Log.w("AyuGram", "File upload failed: " + location);

            process(location);
        }
    }
}
