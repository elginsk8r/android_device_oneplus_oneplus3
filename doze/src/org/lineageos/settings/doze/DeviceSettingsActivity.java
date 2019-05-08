/*
 * Copyright (C) 2015-2016 The CyanogenMod Project
 *               2017 The LineageOS Project
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

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class DeviceSettingsActivity extends PreferenceActivity {

    private static final String TAG_DOZE = "doze";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getFragmentManager().findFragmentByTag(TAG_DOZE) == null) {
            final String action = getIntent().getAction();
            final Fragment fragment;
            if ("org.lineageos.settings.device.DOZE_SETTINGS".equals(action)) {
                fragment = new DozeSettingsFragment();
            } else {
                fragment = new DisplaySettingsFragment();
            }

            getFragmentManager().beginTransaction().replace(android.R.id.content,
                    fragment, TAG_DOZE).commit();
        }
    }
}
