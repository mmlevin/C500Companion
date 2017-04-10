/* C500Companion - An open source, free addition for Ownice C500 head unit
 * Copyright (C) 2017 Maksim M. Levin. Russia, Voronezh
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contacts:
 *            email: mmlevin@mail.ru
*/

package com.wmmaks.c500companion;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;

/**
 * Created by mmlevin on 10.04.2017.
 */

public class BacklightPreference extends DialogPreference {
    private SeekBar seekBarDawn;
    private SeekBar seekBarSunrise;
    private SeekBar seekBarDay;
    private SeekBar seekBarSunset;
    private SeekBar seekBarDusk;
    private SeekBar seekBarNight;
    private Resources resource;

    public BacklightPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.backlight_level);
    }

    @Override
    protected void onBindDialogView(View view) {
        resource = view.getContext().getResources();
        SharedPreferences pref = getSharedPreferences();

        seekBarDawn = (SeekBar) view.findViewById(R.id.seekBarDawn);
        seekBarDawn.setProgress(pref.getInt(resource.getString(R.string.prefBacklightLevelsDawn),resource.getInteger(R.integer.prefBacklightLevelsDawnDefault)));

        seekBarSunrise = (SeekBar) view.findViewById(R.id.seekBarSunrise);
        seekBarSunrise.setProgress(pref.getInt(resource.getString(R.string.prefBacklightLevelsSunrise),resource.getInteger(R.integer.prefBacklightLevelsSunriseDefault)));

        seekBarDay = (SeekBar) view.findViewById(R.id.seekBarDay);
        seekBarDay.setProgress(pref.getInt(resource.getString(R.string.prefBacklightLevelsDay),resource.getInteger(R.integer.prefBacklightLevelsDayDefault)));

        seekBarSunset = (SeekBar) view.findViewById(R.id.seekBarSunset);
        seekBarSunset.setProgress(pref.getInt(resource.getString(R.string.prefBacklightLevelsSunset),resource.getInteger(R.integer.prefBacklightLevelsSunsetDefault)));

        seekBarDusk = (SeekBar) view.findViewById(R.id.seekBarDusk);
        seekBarDusk.setProgress(pref.getInt(resource.getString(R.string.prefBacklightLevelsDusk),resource.getInteger(R.integer.prefBacklightLevelsDuskDefault)));

        seekBarNight = (SeekBar) view.findViewById(R.id.seekBarNight);
        seekBarNight.setProgress(pref.getInt(resource.getString(R.string.prefBacklightLevelsNight),resource.getInteger(R.integer.prefBacklightLevelsNightDefault)));

        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (!positiveResult) return;
        SharedPreferences.Editor editor = getEditor();
        editor.putInt(resource.getString(R.string.prefBacklightLevelsDawn),seekBarDawn.getProgress());
        editor.putInt(resource.getString(R.string.prefBacklightLevelsSunrise),seekBarSunrise.getProgress());
        editor.putInt(resource.getString(R.string.prefBacklightLevelsDay),seekBarDay.getProgress());
        editor.putInt(resource.getString(R.string.prefBacklightLevelsSunset),seekBarSunset.getProgress());
        editor.putInt(resource.getString(R.string.prefBacklightLevelsDusk),seekBarDusk.getProgress());
        editor.putInt(resource.getString(R.string.prefBacklightLevelsNight),seekBarNight.getProgress());
        editor.commit();
        super.onDialogClosed(positiveResult);
    }
}
