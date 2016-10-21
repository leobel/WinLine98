package org.freelectron.leobel.winline98.services;

/**
 * Created by leobel on 10/21/16.
 */
public interface PreferenceService {

    void setHighRecord(Integer record);

    Integer getHighRecord();

    void setAllowTouchSoundPreference(Boolean allowTouchSoundPreference);

    Boolean getAllowTouchSoundPreference();
}
