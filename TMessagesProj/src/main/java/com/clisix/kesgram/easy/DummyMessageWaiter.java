/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.clisix.kesgram.easy;

import android.util.Log;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SendMessagesHelper;

import java.util.ArrayList;

public class DummyMessageWaiter extends EasyWaiter {
    private final ArrayList<Integer> alreadySent;
    private int sendingId;

    public DummyMessageWaiter(int currentAccount) {
        super(currentAccount);

        alreadySent = new ArrayList<>();

        notifications.add(NotificationCenter.messageReceivedByServer);
        notifications.add(NotificationCenter.messageSendError);
        notifications.add(NotificationCenter.messageReceivedByAck);
    }

    public void trySetSendingId(long dialogId) {
        var helper = SendMessagesHelper.getInstance(currentAccount);

        var time = System.currentTimeMillis();

        int sendingId = 0;
        while (sendingId >= 0) {
            try {
                sendingId = helper.getSendingMessageId(dialogId);
            } catch (Exception e) {
                // похуй. телеграм для даунов. пусть крашится.
                // java.lang.ClassCastException: java.lang.Object cannot be cast to org.telegram.tgnet.TLRPC$Message
            }

            if (System.currentTimeMillis() - time > 3500) {
                Log.w("AyuGram", "Failed to get sending message id, possibly sent already");

                unsubscribe();
                break;
            }
        }

        Log.w("AyuGram", "Sending message id: " + sendingId);

        if (sendingId != 0) {
            setSendingId(sendingId);
        }
    }

    private void setSendingId(int sendingId) {
        this.sendingId = sendingId;

        if (!alreadySent.isEmpty()) {
            if (alreadySent.contains(sendingId)) {
                unsubscribe();
            }
        }
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.messageReceivedByAck || id == NotificationCenter.messageReceivedByServer || id == NotificationCenter.messageSendError) {
            var msgId = (Integer) args[0];

            if (sendingId == 0) {
                alreadySent.add(msgId);
                return;
            }

            if (msgId == sendingId) {
                unsubscribe();
            }
        }
    }
}
