/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.clisix.kesgram.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import com.clisix.kesgram.database.dao.DeletedMessageDao;
import com.clisix.kesgram.database.dao.EditedMessageDao;
import com.clisix.kesgram.database.entities.DeletedMessage;
import com.clisix.kesgram.database.entities.DeletedMessageReaction;
import com.clisix.kesgram.database.entities.EditedMessage;

@Database(entities = {
        EditedMessage.class,
        DeletedMessage.class,
        DeletedMessageReaction.class
}, version = 21)
public abstract class AyuDatabase extends RoomDatabase {
    public abstract EditedMessageDao editedMessageDao();

    public abstract DeletedMessageDao deletedMessageDao();
}