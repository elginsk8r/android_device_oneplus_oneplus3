/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017-2019 The LineageOS Project
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

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v14.preference.PreferenceFragment;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;

public class DisplaySettingsFragment extends PreferenceFragment implements OnPreferenceChangeListener {

    private String[] mValidColorModes;

    private SwitchPreference mHighBrighnessPreference;
    private ListPreference mDisplayModePreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.display_settings);
        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mHighBrighnessPreference = (SwitchPreference) findPreference(Utils.DISPLAY_BRIGHTNESS_KEY);
        mDisplayModePreference = (ListPreference) findPreference(Utils.DISPLAY_COLOR_MODE_KEY);

        boolean highBrightnessEnabled = Utils.getHightBrightnessMode(getActivity());
        mHighBrighnessPreference.setChecked(highBrightnessEnabled);

        String colorMode = Utils.getDisplayMode(getActivity());
        mDisplayModePreference.setValue(colorMode);
        mDisplayModePreference.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mHighBrighnessPreference) {
            Utils.setHightBrightnessMode(getActivity(), (Boolean) newValue);
        }

        if (preference == mDisplayModePreference) {
            Utils.setDisplayMode(getActivity(), (String) newValue);
        }

        return true;
    }
}
