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
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;

import java.io.File;
import java.util.ArrayList;

public class DummyFileLoadWaiter extends EasyWaiter {

    private final ArrayList<MessageObject> messages;

    public DummyFileLoadWaiter(int currentAccount, ArrayList<MessageObject> messages) {
        super(currentAccount);
        this.messages = messages;

        notifications.add(NotificationCenter.fileLoaded);
        notifications.add(NotificationCenter.fileLoadFailed);
    }

    private void process(String name) {
        for (var message : messages) {
            var docName = message.getDocumentName();
            var docFilename = FileLoader.getAttachFileName(message.getDocument());

            var res = false;
            if (!TextUtils.isEmpty(docName) && docName.equals(name)) {
                res = true;
            } else if (!TextUtils.isEmpty(docFilename) && docFilename.contains(name)) {
                res = true;
            } else if (!TextUtils.isEmpty(name) && name.contains(docFilename)) {
                res = true;
            }

            if (res) {
                messages.remove(message);
                break;
            }
        }

        if (messages.isEmpty()) {
            unsubscribe();
        }
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.fileLoaded) {
            var name = (String) args[0];
            var path = (File) args[1];

            Log.w("AyuGram", "loaded: " + path + " " + name);

            process(name);
        } else if (id == NotificationCenter.fileLoadFailed) {
            var name = (String) args[0];

            Log.w("AyuGram", "failed: " + name);

            process(name);
        }
    }
}
