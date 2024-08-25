/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.clisix.kesgram;

import android.content.Intent;
import android.net.Uri;
import org.telegram.messenger.*;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.BulletinFactory;

public class AyuCustomHandlers {
    public static void handleAyu(BaseFragment fragment) {
        if (fragment == null) {
            return;
        }

        BulletinFactory.of(fragment).createSimpleBulletin(R.raw.info, LocaleController.getString(R.string.SecretMessageTecno)).show();
    }

    public static void handleXiaomi(BaseFragment fragment) {
        if (fragment == null) {
            return;
        }

        if (XiaomiUtilities.isMIUI()) {
            BulletinFactory.of(fragment).createSimpleBulletin(R.raw.info, LocaleController.getString(R.string.SecretMessageXiaomiFailure)).show();

            Intent intent = new Intent(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + BuildConfig.APPLICATION_ID));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ApplicationLoader.applicationContext.startActivity(intent);
        } else {
            BulletinFactory.of(fragment).createSimpleBulletin(R.raw.info, LocaleController.getString(R.string.SecretMessageXiaomiSuccess)).show();
        }
    }
}
