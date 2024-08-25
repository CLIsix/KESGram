/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.clisix.kesgram.sync.models;

public class SyncRead implements SyncEvent {
    public String type = "sync_read";
    public long userId;
    public SyncReadArgs args;

    public static class SyncReadArgs {
        public long dialogId;
        public int untilId;
        public int unread;
    }
}
