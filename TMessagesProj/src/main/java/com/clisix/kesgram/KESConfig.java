package com.clisix.kesgram;

import android.app.Activity;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import org.telegram.messenger.*;

import java.util.ArrayList;
import java.util.Arrays;

public class KESConfig {

    public static SharedPreferences preferences;

    public static String getKES() {
        return preferences.getString("KESServerURL", KESConstants.DEFAULT_KES);
    }
}
