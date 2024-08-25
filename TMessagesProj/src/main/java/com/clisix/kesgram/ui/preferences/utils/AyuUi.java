/*
 * This is the source code of AyuGram for Android.
 *
 * We do not and cannot prevent the use of our code,
 * but be respectful and credit the original author.
 *
 * Copyright @Radolyn, 2023
 */

package com.clisix.kesgram.ui.preferences.utils;

import android.app.Activity;
import android.widget.LinearLayout;
import com.clisix.kesgram.AyuConfig;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.Cells.EditTextSettingsCell;
import org.telegram.ui.Cells.TextCell;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class AyuUi {
    public static void spawnEditBox(Activity parent, TextCell view, String title, Supplier<String> getter, String configField, String defaultValue) {
        spawnEditBox(parent,
                view,
                title,
                getter,
                configField,
                defaultValue,
                (s) -> {
                },
                (s) -> s
        );
    }

    public static void spawnEditBox(Activity parent, TextCell view, String title, Supplier<String> getter, String configField, String defaultValue, Consumer<String> callback) {
        spawnEditBox(parent,
                view,
                title,
                getter,
                configField,
                defaultValue,
                callback,
                (s) -> s
        );
    }

    public static void spawnEditBox(Activity parent, TextCell view, String title, Supplier<String> getter, String configField, String defaultValue, Consumer<String> callback, Function<String, String> map) {
        var builder = new AlertDialog.Builder(parent);
        builder.setTitle(title);
        var layout = new LinearLayout(parent);
        var input = new EditTextSettingsCell(parent);
        input.setText(getter.get(), true);

        layout.setGravity(LinearLayout.VERTICAL);
        layout.addView(input);
        builder.setView(layout);
        builder.setPositiveButton(LocaleController.getString("Save", R.string.Save), (dialog, which) -> {
            var s = map.apply(input.getText());

            AyuConfig.editor.putString(configField, s).apply();
            view.setTextAndValue(title, s, true);
            callback.accept(s);
        });
        builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), (dialog, which) -> dialog.cancel());
        builder.setNeutralButton(LocaleController.getString("Reset", R.string.Reset), (dialog, which) -> {
            var s = map.apply(defaultValue);

            AyuConfig.editor.putString(configField, s).apply();
            view.setTextAndValue(title, s, true);
            callback.accept(s);
        });

        builder.show();
    }
}
