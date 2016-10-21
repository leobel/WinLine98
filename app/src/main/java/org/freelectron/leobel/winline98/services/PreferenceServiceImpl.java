package org.freelectron.leobel.winline98.services;

import android.content.SharedPreferences;

/**
 * Created by leobel on 10/21/16.
 */
public class PreferenceServiceImpl implements PreferenceService {

    private static String HIGH_RECORD = "HIGH_RECORD";

    private final SharedPreferences sharedPreferences;

    public PreferenceServiceImpl(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void setHighRecord(Integer record) {
        SharedPreferences.Editor  editor =  sharedPreferences.edit();
        editor.putInt(HIGH_RECORD, record);
        editor.commit();
    }

    @Override
    public Integer getHighRecord() {
        return sharedPreferences.getInt(HIGH_RECORD, 0);
    }

    @Override
    public void setAllowTouchSoundPreference(Boolean allowTouchSoundPreference) {

    }

    @Override
    public Boolean getAllowTouchSoundPreference() {
        return null;
    }
}
