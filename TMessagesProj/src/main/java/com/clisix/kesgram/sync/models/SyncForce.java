/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.clisix.kesgram.sync.models;

public class SyncForce implements SyncEvent {
    public String type = "sync_force";
    public long userId;
    public SyncForce.SyncForceArgs args;

    public static class SyncForceArgs {
        public int fromDate;
    }
}
