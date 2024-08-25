/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.clisix.kesgram.easy;

import com.clisix.kesgram.utils.AyuGhostUtils;
import org.telegram.messenger.*;
import org.telegram.tgnet.TLRPC;

import java.util.ArrayList;
import java.util.HashMap;

public class AyuEasyUtils {
    public static void loadDocumentsSync(int currentAccount, ArrayList<MessageObject> toBeDownloaded) {
        var waiter = new DummyFileLoadWaiter(currentAccount, toBeDownloaded);
        waiter.subscribe();

        var fileLoader = FileLoader.getInstance(currentAccount);

        for (var message : toBeDownloaded) {
            var doc = message.getDocument();
            var photo = MessageObject.getPhoto(message.messageOwner);

            if (doc != null) {
                fileLoader.loadFile(doc, message, FileLoader.PRIORITY_HIGH, 0);
            } else if (photo != null) {
                var path = ImageLocation.getForPhoto(FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize()), photo);
                fileLoader.loadFile(path, message, null, FileLoader.PRIORITY_HIGH, 0);
            }
        }

        waiter.await();
    }

    public static void sendTextMessageSync(
            int currentAccount,
            String message,
            long peer,
            MessageObject replyToMsg,
            MessageObject replyToTopMsg,
            TLRPC.WebPage webPage,
            boolean searchLinks,
            ArrayList<TLRPC.MessageEntity> entities,
            boolean notify
    ) {
        var dialogId = AyuGhostUtils.getDialogId(MessagesController.getInstance(currentAccount).getInputPeer(peer));

        var waiter = new DummyMessageWaiter(currentAccount);
        waiter.subscribe();

        var helper = SendMessagesHelper.getInstance(currentAccount);

        AndroidUtilities.runOnUIThread(() -> {
            helper.sendMessage(
                    message,
                    peer,
                    replyToMsg,
                    replyToTopMsg,
                    webPage,
                    searchLinks,
                    entities,
                    null,
                    null,
                    notify,
                    0,
                    null,
                    false
            );
        });

        waiter.trySetSendingId(dialogId);

        waiter.await();
    }

    public static void sendDocumentMessageSync(
            int currentAccount,
            TLRPC.TL_document document,
            String path,
            long peer,
            MessageObject replyToMsg,
            MessageObject replyToTopMsg,
            String caption,
            ArrayList<TLRPC.MessageEntity> entities,
            boolean notify,
            Long groupId,
            boolean isFinalInGroup
    ) {
        var dialogId = AyuGhostUtils.getDialogId(MessagesController.getInstance(currentAccount).getInputPeer(peer));

        var waitToSend = groupId == 0 || isFinalInGroup;
        var sendWaiter = new DummyMessageWaiter(currentAccount);
        if (waitToSend) {
            sendWaiter.subscribe();
        }

        var uploadWaiter = new DummyFileUploadWaiter(currentAccount, path);
        uploadWaiter.subscribe();

        var helper = SendMessagesHelper.getInstance(currentAccount);

        var params = createParams(groupId, isFinalInGroup);

        AndroidUtilities.runOnUIThread(() -> {
            helper.sendMessage(
                    document,
                    null,
                    path,
                    peer,
                    replyToMsg,
                    replyToTopMsg,
                    caption,
                    entities,
                    null,
                    params,
                    notify,
                    0,
                    0,
                    null,
                    null,
                    false
            );
        });

        if (waitToSend) {
            sendWaiter.trySetSendingId(dialogId);
        }

        if (groupId == 0) {
            uploadWaiter.await();
        }

        if (waitToSend) {
            sendWaiter.await();
        }
    }

    public static void sendPhotoMessageSync(
            int currentAccount,
            TLRPC.TL_photo photo,
            String path,
            long peer,
            MessageObject replyToMsg,
            MessageObject replyToTopMsg,
            String caption,
            ArrayList<TLRPC.MessageEntity> entities,
            boolean notify,
            Long groupId,
            boolean isFinalInGroup
    ) {
        // why? because.
        var bigPhoto = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.getPhotoSize());
        var filename = FileLoader.getDirectory(FileLoader.MEDIA_DIR_CACHE) + "/" + bigPhoto.location.volume_id + "_" + bigPhoto.location.local_id + ".jpg";

        var dialogId = AyuGhostUtils.getDialogId(MessagesController.getInstance(currentAccount).getInputPeer(peer));

        var waitToSend = groupId == 0 || isFinalInGroup;
        var sendWaiter = new DummyMessageWaiter(currentAccount);
        if (waitToSend) {
            sendWaiter.subscribe();
        }

        var uploadWaiter = new DummyFileUploadWaiter(currentAccount, filename);
        uploadWaiter.subscribe();

        var helper = SendMessagesHelper.getInstance(currentAccount);

        var params = createParams(groupId, isFinalInGroup);

        AndroidUtilities.runOnUIThread(() -> {
            helper.sendMessage(
                    photo,
                    path,
                    peer,
                    replyToMsg,
                    replyToTopMsg,
                    caption,
                    entities,
                    null,
                    params,
                    notify,
                    0,
                    0,
                    null,
                    false,
                    false
            );
        });

        if (waitToSend) {
            sendWaiter.trySetSendingId(dialogId);
        }

        if (groupId == 0) {
            uploadWaiter.await();
        }

        if (waitToSend) {
            sendWaiter.await();
        }
    }

    public static void forwardMessagesSync(
            int currentAccount,
            ArrayList<MessageObject> messages,
            final long peer,
            boolean forwardFromMyName,
            boolean hideCaption,
            boolean notify,
            int scheduleDate,
            MessageObject replyToTopMsg
    ) {
        var dialogId = AyuGhostUtils.getDialogId(MessagesController.getInstance(currentAccount).getInputPeer(peer));

        var waiter = new DummyMessageWaiter(currentAccount);
        waiter.subscribe();

        var helper = SendMessagesHelper.getInstance(currentAccount);

        AndroidUtilities.runOnUIThread(() -> {
            helper.sendMessage(messages, peer, forwardFromMyName, hideCaption, notify, scheduleDate, replyToTopMsg);
        });

        waiter.trySetSendingId(dialogId);

        waiter.await();
    }

    private static HashMap<String, String> createParams(long groupId, boolean isFinalInGroup) {
        if (groupId == 0) {
            return null;
        }

        var params = new HashMap<String, String>();

        params.put("groupId", String.valueOf(groupId));

        if (isFinalInGroup) {
            params.put("final", "1");
        }

        return params;
    }
}
