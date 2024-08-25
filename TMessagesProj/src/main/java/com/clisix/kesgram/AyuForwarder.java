/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.clisix.kesgram;

import android.text.TextUtils;
import android.util.LongSparseArray;
import com.exteragram.messenger.utils.ChatUtils;
import com.google.android.exoplayer2.util.Log;
import com.clisix.kesgram.easy.AyuEasyUtils;
import org.telegram.messenger.*;
import org.telegram.tgnet.TLRPC;

import java.io.File;
import java.util.ArrayList;

// music for coding on jaBBa
// https://open.spotify.com/track/2qpOzuQGFqTKNn56w7qShx
public class AyuForwarder {
    public static boolean isFullAyuForwardsNeeded(int currentAccount, ArrayList<MessageObject> messages) {
        var dialogId = messages.get(0).getDialogId();
        var chat = MessagesController.getInstance(currentAccount).getChat(Math.abs(dialogId));

        return chat != null && chat.ayuNoforwards;
    }

    public static boolean isAyuForwardNeeded(ArrayList<MessageObject> messages) {
        for (var message : messages) {
            if (isAyuForwardNeeded(message)) {
                return true;
            }
        }

        return false;
    }

    private static boolean isAyuForwardNeeded(MessageObject message) {
        return message.messageOwner != null && (message.messageOwner.ayuDeleted || message.messageOwner.ayuNoforwards);
    }

    public static void intelligentForward(int currentAccount, ArrayList<MessageObject> messages, long peer, boolean forwardFromMyName, boolean hideCaption, boolean notify, int scheduleDate, MessageObject replyToTopMsg) {
        var batches = new ArrayList<ForwardBatch>();

        var currentArray = new ArrayList<MessageObject>();
        var currentBatch = new ForwardBatch(isAyuForwardNeeded(messages.get(0)), currentArray);

        for (var message : messages) {
            if (isAyuForwardNeeded(message) != currentBatch.isAyuForwardNeeded) {
                batches.add(currentBatch);

                currentArray = new ArrayList<>();
                currentBatch = new ForwardBatch(isAyuForwardNeeded(message), currentArray);
            }

            currentArray.add(message);
        }

        batches.add(currentBatch);

        for (var batch : batches) {
            if (batch.isAyuForwardNeeded) {
                forwardMessages(currentAccount, batch.messages, peer, forwardFromMyName, hideCaption, notify, scheduleDate, replyToTopMsg);
            } else {
                // use default forward, but wait for it
                AyuEasyUtils.forwardMessagesSync(currentAccount, batch.messages, peer, forwardFromMyName, hideCaption, notify, scheduleDate, replyToTopMsg);
            }
        }
    }

    public static void forwardMessages(int currentAccount, ArrayList<MessageObject> messages, long peer, boolean forwardFromMyName, boolean hideCaption, boolean notify, int scheduleDate, MessageObject replyToTopMsg) {
        var toBeDownloaded = new ArrayList<MessageObject>();

        var groups = new LongSparseArray<Long>();
        var groupIds = new LongSparseArray<Long>();

        // todo: replies

        var fullNoforwards = isFullAyuForwardsNeeded(currentAccount, messages);

        for (var message : messages) {
            if (fullNoforwards || message.messageOwner.ayuNoforwards || message.messageOwner.ayuDeleted) {
                if (AyuUtils.isMediaDownloadable(message, false)) {
                    toBeDownloaded.add(message);
                }
            }

            if (message.getGroupId() != 0) {
                var currentCount = groups.get(message.getGroupId());
                groups.put(message.getGroupId(), currentCount == null ? 1 : currentCount + 1);

                var currentId = groupIds.get(message.getGroupId());
                if (currentId == null) {
                    groupIds.put(message.getGroupId(), Utilities.random.nextLong());
                }
            }
        }

        if (!toBeDownloaded.isEmpty()) {
            AyuEasyUtils.loadDocumentsSync(currentAccount, toBeDownloaded);
        }

        for (var message : messages) {
            var textExtracted = ChatUtils.getMessageText(message, null);
            var text = textExtracted == null ? "" : textExtracted.toString();
            var mediaDownloadable = AyuUtils.isMediaDownloadable(message, false);
            var groupId = message.getGroupId();
            var newGroupId = groupIds.get(groupId, 0L);

            var isFinalInGroup = false;
            if (groupId != 0) {
                groups.put(groupId, groups.get(groupId) - 1);
                isFinalInGroup = groups.get(groupId) == 0;
            }

            var messagePath = FileLoader.getInstance(currentAccount).getPathToMessage(message.messageOwner);
            // todo: if not exists, try to take from cache forcefully

            if (TextUtils.isEmpty(text) && !mediaDownloadable) {
                continue;
            }

            if (!mediaDownloadable) {
                AyuEasyUtils.sendTextMessageSync(
                        currentAccount,
                        text,
                        peer,
                        null,
                        replyToTopMsg,
                        null, // todo
                        false,
                        message.messageOwner.entities,
                        notify
                );
            } else if (message.getDocument() != null) {
                var doc = message.getDocument();
                var newDoc = mapDocument(currentAccount, doc, messagePath);

                AyuEasyUtils.sendDocumentMessageSync(
                        currentAccount,
                        newDoc,
                        messagePath.getAbsolutePath(),
                        peer,
                        null,
                        replyToTopMsg,
                        text,
                        message.messageOwner.entities,
                        notify,
                        newGroupId,
                        isFinalInGroup
                );
            } else if (message.isPhoto()) {
                var photo = MessageObject.getPhoto(message.messageOwner);
                var newPhoto = mapPhoto(currentAccount, photo, messagePath);

                var caption = TextUtils.isEmpty(photo.caption) ? text : photo.caption;

                AyuEasyUtils.sendPhotoMessageSync(
                        currentAccount,
                        newPhoto,
                        messagePath.getAbsolutePath(),
                        peer,
                        null,
                        replyToTopMsg,
                        caption,
                        message.messageOwner.entities,
                        notify,
                        newGroupId,
                        isFinalInGroup
                );
            } else {
                Log.w("AyuGram", "Unsupported message type: " + message.messageOwner);
            }

            Log.w("AyuGram", "Message forwarded");
        }

        Log.w("AyuGram", "All messages forwarded");
    }

    private static TLRPC.TL_document mapDocument(int currentAccount, TLRPC.Document doc, File messagePath) {
        var newDoc = new TLRPC.TL_document();
        newDoc.file_reference = new byte[0];
        newDoc.dc_id = Integer.MIN_VALUE;
        newDoc.user_id = doc.user_id;
        newDoc.mime_type = doc.mime_type;
        newDoc.file_name = doc.file_name;
        newDoc.file_name_fixed = doc.file_name_fixed;
        newDoc.date = AccountInstance.getInstance(currentAccount).getConnectionsManager().getCurrentTime();
        newDoc.size = (int) messagePath.length();

        newDoc.attributes = doc.attributes;
        newDoc.localPath = messagePath.getAbsolutePath();

        return newDoc;
    }

    private static TLRPC.TL_photo mapPhoto(int currentAccount, TLRPC.Photo photo, File messagePath) {
        var helper = SendMessagesHelper.getInstance(currentAccount);

        var newPhoto = helper.generatePhotoSizes(messagePath.getAbsolutePath(), null); // todo: mapPhoto
        newPhoto.flags = photo.flags;
        newPhoto.has_stickers = photo.has_stickers;
        newPhoto.date = AccountInstance.getInstance(currentAccount).getConnectionsManager().getCurrentTime();
        newPhoto.geo = photo.geo;
        newPhoto.caption = photo.caption;

        return newPhoto;
    }

    private static class ForwardBatch {
        public final boolean isAyuForwardNeeded;
        public final ArrayList<MessageObject> messages;

        public ForwardBatch(boolean isAyuForwardNeeded, ArrayList<MessageObject> messages) {
            this.isAyuForwardNeeded = isAyuForwardNeeded;
            this.messages = messages;
        }
    }
}
