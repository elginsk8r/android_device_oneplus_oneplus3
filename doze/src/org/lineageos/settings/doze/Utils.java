/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017-2018 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.lineageos.settings.doze;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.UserHandle;
import android.support.v7.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.android.internal.hardware.AmbientDisplayConfiguration;
import com.evervolv.internal.util.FileUtils;

import java.util.List;

import static android.provider.Settings.Secure.DOZE_ALWAYS_ON;
import static android.provider.Settings.Secure.DOZE_ENABLED;

public final class Utils {

    private static final String TAG = "DozeUtils";
    private static final boolean DEBUG = false;

    private static final String DOZE_INTENT = "com.android.systemui.doze.pulse";

    protected static final String ALWAYS_ON_DISPLAY = "always_on_display";
    protected static final String GESTURE_PICK_UP_KEY = "gesture_pick_up";
    protected static final String GESTURE_HAND_WAVE_KEY = "gesture_hand_wave";
    protected static final String GESTURE_POCKET_KEY = "gesture_pocket";

    protected static final String DISPLAY_PATH = "/sys/devices/virtual/graphics/fb0/";
    protected static final String DISPLAY_BRIGHTNESS_KEY = "high_brightness";
    protected static final String DISPLAY_COLOR_MODE_KEY = "color_mode";

    protected static void startService(Context context) {
        if (DEBUG) Log.d(TAG, "Starting service");
        context.startServiceAsUser(new Intent(context, DozeService.class),
                UserHandle.CURRENT);
    }

    protected static void stopService(Context context) {
        if (DEBUG) Log.d(TAG, "Stopping service");
        context.stopServiceAsUser(new Intent(context, DozeService.class),
                UserHandle.CURRENT);
    }

    protected static void checkDozeService(Context context) {
        if (isDozeEnabled(context) && sensorsEnabled(context) && !isAlwaysOnEnabled(context)) {
            startService(context);
        } else {
            stopService(context);
        }
    }

    protected static boolean isDozeEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(),
                DOZE_ENABLED, 1) != 0;
    }

    protected static boolean enableDoze(Context context, boolean enable) {
        return Settings.Secure.putInt(context.getContentResolver(),
                DOZE_ENABLED, enable ? 1 : 0);
    }

    protected static void launchDozePulse(Context context) {
        if (DEBUG) Log.d(TAG, "Launch doze pulse");
        context.sendBroadcastAsUser(new Intent(DOZE_INTENT),
                new UserHandle(UserHandle.USER_CURRENT));
    }

    protected static boolean enableAlwaysOn(Context context, boolean enable) {
        return Settings.Secure.putIntForUser(context.getContentResolver(),
                DOZE_ALWAYS_ON, enable ? 1 : 0, UserHandle.USER_CURRENT);
    }

    protected static boolean isAlwaysOnEnabled(Context context) {
        final boolean enabledByDefault = context.getResources()
                .getBoolean(com.android.internal.R.bool.config_dozeAlwaysOnEnabled);

        return Settings.Secure.getIntForUser(context.getContentResolver(),
                DOZE_ALWAYS_ON, alwaysOnDisplayAvailable(context) && enabledByDefault ? 1 : 0,
                UserHandle.USER_CURRENT) != 0;
    }

    protected static boolean alwaysOnDisplayAvailable(Context context) {
        return new AmbientDisplayConfiguration(context).alwaysOnAvailable();
    }

    protected static boolean isGestureEnabled(Context context, String gesture) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(gesture, false);
    }

    protected static boolean isPickUpEnabled(Context context) {
        return isGestureEnabled(context, GESTURE_PICK_UP_KEY);
    }

    protected static boolean isHandwaveGestureEnabled(Context context) {
        return isGestureEnabled(context, GESTURE_HAND_WAVE_KEY);
    }

    protected static boolean isPocketGestureEnabled(Context context) {
        return isGestureEnabled(context, GESTURE_POCKET_KEY);
    }

    protected static boolean sensorsEnabled(Context context) {
        return isPickUpEnabled(context) || isHandwaveGestureEnabled(context)
                || isPocketGestureEnabled(context);
    }

    protected static Sensor findSensorWithType(SensorManager sensorManager, String type) {
        if (TextUtils.isEmpty(type)) {
            return null;
        }
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor s : sensorList) {
            if (type.equals(s.getStringType())) {
                return s;
            }
        }
        return null;
    }

    protected static boolean getHightBrightnessMode(Context context) {
        return FileUtils.readOneLine(DISPLAY_PATH + "hbm").equals("1");
    }

    protected static void setHightBrightnessMode(Context context, boolean enabled) {
        if (FileUtils.isFileWritable(DISPLAY_PATH + "hbm")) {
            FileUtils.writeLine(DISPLAY_PATH + "hbm", enabled ? "1" : "0");
        }
    }

    protected static String getDisplayMode(Context context) {
        String[] validColorModes = context.getResources().getStringArray(
                R.array.color_mode_entry_values);
        for (String key : validColorModes) {
            String mode = FileUtils.readOneLine(DISPLAY_PATH + key);
            if (mode.equals("1")) {
                return key;
            }
        }
        return null;
    }

    protected static void setDisplayMode(Context context, String mode) {
        String[] validColorModes = context.getResources().getStringArray(
                R.array.color_mode_entry_values);
        for (String key : validColorModes) {
            if (FileUtils.isFileWritable(DISPLAY_PATH + key)) {
                FileUtils.writeLine(DISPLAY_PATH + key, mode.equals(key) ? "1" : "0");
            }
        }
    }

    protected static void restoreSettings(Context context) {
        boolean brightnessMode = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(DISPLAY_BRIGHTNESS_KEY, false);
        setHightBrightnessMode(context, brightnessMode);

        String colorMode = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(DISPLAY_COLOR_MODE_KEY, "off");
        setDisplayMode(context, colorMode);

        checkDozeService(context);
    }
}
