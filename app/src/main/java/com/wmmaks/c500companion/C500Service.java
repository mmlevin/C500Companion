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

import android.Manifest;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.wmmaks.utils.SunCalc;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class C500Service extends IntentService {
    private static final String LOG_TAG = "C500Companion";
    private static final String PREFS_NAME = "C500CompanionPreferences";
    private static final String PREFS_MODE = "C500Mode";
    private static final String PREFS_LATITUDE = "C500CompanionLatitude";
    private static final String PREFS_LONGITUDE = "C500CompanionLongitude";

    private static final String ACTION = "com.wmmaks.c500companion.ACTION";
    private static final String ACTION_KEY = "com.wmmaks.c500companion.KEY";

    private static final String CMD = "CMD";
    private static final String KEY = "KEY";

    private static final int CMD_MODE_ENTER_SLEEP = 1;
    private static final int CMD_MODE_RESTORE_SLEEP = 2;
    private static final int CMD_MODE_CHANGE = 3;
    private static final int CMD_MODE_SEEK_UP = 4;
    private static final int CMD_MODE_SEEK_DOWN = 5;
    private static final int CMD_BACKLIGHT_UPDATE = 128;
    private static final int CMD_LOCATION_UPDATE = 129;

    public static final String POWERAMP_API_COMMAND = "com.maxmpz.audioplayer.API_COMMAND";
    public static final String POWERAMP_PACKAGE_NAME = "com.maxmpz.audioplayer";
    public static final String POWERAMP_API_COMMAND_CMD = "cmd";
    public static final int POWERAMP_API_COMMAND_TOGGLE_PLAY_PAUSE = 1;
    public static final int POWERAMP_API_COMMAND_PAUSE = 2;
    public static final int POWERAMP_API_COMMAND_RESUME = 3;
    public static final int POWERAMP_API_COMMAND_NEXT = 4;
    public static final int POWERAMP_API_COMMAND_PREVIOUS = 5;

    private SharedPreferences settings;
    private int mMode;
    double lat, lng;

    private C500Helper.C500_MODES mModes[] = {
            C500Helper.C500_MODES.C500_RADIO,
            C500Helper.C500_MODES.C500_MUSIC,
            C500Helper.C500_MODES.C500_AVIN
    };

    enum BACKLIGHT_INDEX {
        BACKLIGHT_INDEX_DAWN,
        BACKLIGHT_INDEX_SUNRISE,
        BACKLIGHT_INDEX_DAY,
        BACKLIGHT_INDEX_SUNSET,
        BACKLIGHT_INDEX_DUSK,
        BACKLIGHT_INDEX_NIGHT
    }

    public C500Service() {
        super("C500Service");
    }

    public static void KeyPressDownAndUp(int key, Context context) {
        long eventtime = SystemClock.uptimeMillis() - 1;

        Intent downIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent downEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_DOWN, key, 0);
        downIntent.putExtra(Intent.EXTRA_KEY_EVENT, downEvent);
        context.sendOrderedBroadcast(downIntent, null);

        eventtime++;
        Intent upIntent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
        KeyEvent upEvent = new KeyEvent(eventtime, eventtime, KeyEvent.ACTION_UP, key, 0);
        upIntent.putExtra(Intent.EXTRA_KEY_EVENT, upEvent);
        context.sendOrderedBroadcast(upIntent, null);
    }

    public void openApplication(Context context, String packageName) {
        PackageInfo pi;
        try {
            pi = context.getPackageManager().getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            pi = null;
            e.printStackTrace();
        }
        if (pi != null) {
            Intent resolveIntent = new Intent("android.intent.action.MAIN", null);
            resolveIntent.setPackage(pi.packageName);
            ResolveInfo ri = (ResolveInfo) context.getPackageManager().queryIntentActivities(resolveIntent, 0).iterator().next();
            if (ri != null) {
                packageName = ri.activityInfo.packageName;
                String className = ri.activityInfo.name;
                Intent intent = new Intent("android.intent.action.MAIN");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(new ComponentName(packageName, className));
                context.startActivity(intent);
            }
        }
    }

    private void toast(final String text, final int duration) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), text, duration).show();
            }
        });
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION.equals(action)) {
                int param = intent.getIntExtra(CMD, 0);
                RestoreState();
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

                boolean usePowerAmp = sharedPref.getBoolean(getString(R.string.prefPowerAmpUse), getResources().getBoolean(R.bool.prefPowerAmpUseDefault));
                boolean usePowerAmpAPI = sharedPref.getBoolean(getString(R.string.prefPowerAmpUseApi), getResources().getBoolean(R.bool.prefPowerAmpUseApiDefault));
                boolean pauseOnSleep = sharedPref.getBoolean(getString(R.string.prefPowerAmpPauseOnSleep), getResources().getBoolean(R.bool.prefPowerAmpPauseOnSleepDefault));
                boolean playOnWakeup = sharedPref.getBoolean(getString(R.string.prefPowerAmpPlayOnWakeup), getResources().getBoolean(R.bool.prefPowerAmpPlayOnWakeupDefault));
                boolean switchWithSeek = sharedPref.getBoolean(getString(R.string.prefPowerAmpSwitchWithSeek), getResources().getBoolean(R.bool.prefPowerAmpSwitchWithSeekDefault));
                boolean launchDirect = sharedPref.getBoolean(getString(R.string.prefPowerAmpLaunchDirect), getResources().getBoolean(R.bool.prefPowerAmpLaunchDirectDefault));
                boolean turnOffBluetooth = sharedPref.getBoolean(getString(R.string.prefBluetoothTurnOff), getResources().getBoolean(R.bool.prefBluetoothTurnOffDefault));

                switch (param) {
                    case CMD_MODE_ENTER_SLEEP:
                        Log.d(LOG_TAG, "Received ENTER_SLEEP");
                        if (pauseOnSleep) {
                            if (usePowerAmp && usePowerAmpAPI) {
                                intent = new Intent(POWERAMP_API_COMMAND);
                                intent.setPackage(POWERAMP_PACKAGE_NAME);
                                intent.putExtra(POWERAMP_API_COMMAND_CMD, POWERAMP_API_COMMAND_PAUSE);
                                startService(intent);
                            } else {
                                KeyPressDownAndUp(KeyEvent.KEYCODE_MEDIA_PAUSE, this);
                            }
                        }
                        break;
                    case CMD_MODE_RESTORE_SLEEP:
                        Log.d(LOG_TAG, "Received RESTORE_SLEEP");

                        UpdateBacklight();

                        Intent alarmIntent = new Intent (ACTION);
                        alarmIntent.setClassName(getPackageName(), getClass().getName());
                        alarmIntent.putExtra(CMD,CMD_BACKLIGHT_UPDATE);

                        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                SystemClock.elapsedRealtime() + 60 * 1000, 60 * 1000, PendingIntent.getService(this,CMD_BACKLIGHT_UPDATE,alarmIntent,0));

                        if (mModes[mMode] == C500Helper.C500_MODES.C500_MUSIC) {
                            SetMode(mModes[mMode], usePowerAmp,launchDirect);
                            if (playOnWakeup) {
                                if (usePowerAmp && usePowerAmpAPI) {
                                    intent = new Intent(POWERAMP_API_COMMAND);
                                    intent.setPackage(POWERAMP_PACKAGE_NAME);
                                    intent.putExtra(POWERAMP_API_COMMAND_CMD, POWERAMP_API_COMMAND_RESUME);
                                    startService(intent);
                                } else {
                                    KeyPressDownAndUp(KeyEvent.KEYCODE_MEDIA_PLAY, this);
                                }
                            }
                        }

                        if (turnOffBluetooth) {
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                    if (mBluetoothAdapter.isEnabled()){
                                        mBluetoothAdapter.disable();
                                    }
                                }
                            }, 5000);
                        }
                        break;
                    case CMD_MODE_CHANGE:
                        Log.d(LOG_TAG,"Received mode change");
                        if (++mMode >= mModes.length) {mMode = 0;}
                        SetMode(mModes[mMode],usePowerAmp,launchDirect);
                        break;
                    case CMD_MODE_SEEK_DOWN:
                        Log.d(LOG_TAG,"Received SEEK_DOWN");
                        if (switchWithSeek) {
                            if (usePowerAmp && usePowerAmpAPI) {
                                intent = new Intent(POWERAMP_API_COMMAND);
                                intent.setPackage(POWERAMP_PACKAGE_NAME);
                                intent.putExtra(POWERAMP_API_COMMAND_CMD, POWERAMP_API_COMMAND_PREVIOUS);
                                startService(intent);
                            } else {
                                KeyPressDownAndUp(KeyEvent.KEYCODE_MEDIA_PREVIOUS, this);
                            }
                        }
                        break;
                    case CMD_MODE_SEEK_UP:
                        Log.d(LOG_TAG,"Received SEEK_UP");
                        if (switchWithSeek) {
                            if (usePowerAmp && usePowerAmpAPI) {
                                intent = new Intent(POWERAMP_API_COMMAND);
                                intent.setPackage(POWERAMP_PACKAGE_NAME);
                                intent.putExtra(POWERAMP_API_COMMAND_CMD, POWERAMP_API_COMMAND_NEXT);
                                startService(intent);
                            } else {
                                KeyPressDownAndUp(KeyEvent.KEYCODE_MEDIA_NEXT, this);
                            }
                        }
                        break;
                    case CMD_BACKLIGHT_UPDATE:
                        UpdateBacklight();
                        break;
                    case CMD_LOCATION_UPDATE:
                        if (intent.hasExtra(LocationManager.KEY_LOCATION_CHANGED)) {
                            Location location = (Location) intent.getExtras().get(LocationManager.KEY_LOCATION_CHANGED);
                            if (location.hasAccuracy()) {
                                if (settings == null) settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                                SharedPreferences.Editor editor = settings.edit();
                                lat = location.getLatitude();
                                lng = location.getLongitude();
                                editor.putFloat(PREFS_LATITUDE, (float) lat);
                                editor.putFloat(PREFS_LONGITUDE, (float) lng);
                                editor.apply();
                                Log.d(LOG_TAG,String.format("Location: %f,%f",lat,lng));
                            }
                        }
                        break;
                }
                SaveState();
            } else if (ACTION_KEY.equals(action)) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

                boolean useKeyDebug = sharedPref.getBoolean(getString(R.string.prefPowerAmpUse), getResources().getBoolean(R.bool.prefPowerAmpUseDefault));
                if (useKeyDebug) {
                    int key = intent.getIntExtra(KEY, 0);
                    toast (getString(R.string.toast_key_code,key), Toast.LENGTH_SHORT);
                }

            } else
            {
                Log.d(LOG_TAG,"Received: " + action);
            }
        }
    }

    private void SetMode(C500Helper.C500_MODES mode, boolean usePowerAmp, boolean launchDirect) {
        Log.d(LOG_TAG,"Switching to " + mode.name());
        Intent intent;
        switch (mode) {
            case C500_MUSIC:
                if (usePowerAmp) {
                    if (launchDirect) {
                        openApplication(this, POWERAMP_PACKAGE_NAME);
                    } else {
                        intent = new Intent (C500Helper.ACTION_RECOGNIZE_CMD);
                        intent.putExtra(C500Helper.ACTION_RECOGNIZE_CMD_DOMAIN, C500Helper.ACTION_RECOGNIZE_CMD_DOMAIN_APP);
                        intent.putExtra(C500Helper.ACTION_RECOGNIZE_CMD_DOMAIN_APP_EXTRA_PACKAGE, POWERAMP_PACKAGE_NAME);
                        intent.putExtra(C500Helper.ACTION_RECOGNIZE_CMD_DOMAIN_APP_ACTION, C500Helper.ACTION_RECOGNIZE_CMD_DOMAIN_APP_ACTION_OPEN);
                        sendBroadcast(intent);
                    }
                } else {
                    intent = new Intent (C500Helper.ACTION_RECOGNIZE_CMD);
                    intent.putExtra(C500Helper.ACTION_RECOGNIZE_CMD_DOMAIN, C500Helper.ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE);
                    intent.putExtra(C500Helper.ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME, C500Helper.ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME_MUSIC);
                    intent.putExtra(C500Helper.ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_ACTION, C500Helper.ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_ACTION_OPEN);
                    sendBroadcast(intent);
                }
                break;
            case C500_RADIO:
                intent = new Intent (C500Helper.ACTION_RECOGNIZE_CMD);
                intent.putExtra(C500Helper.ACTION_RECOGNIZE_CMD_DOMAIN, C500Helper.ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE);
                intent.putExtra(C500Helper.ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME, C500Helper.ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME_RADIO);
                intent.putExtra(C500Helper.ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_ACTION, C500Helper.ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_ACTION_OPEN);
                sendBroadcast(intent);
                break;
            case C500_AVIN:
                intent = new Intent (C500Helper.ACTION_RECOGNIZE_CMD);
                intent.putExtra(C500Helper.ACTION_RECOGNIZE_CMD_DOMAIN, C500Helper.ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE);
                intent.putExtra(C500Helper.ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME, C500Helper.ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME_AVIN);
                intent.putExtra(C500Helper.ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_ACTION, C500Helper.ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_ACTION_OPEN);
                sendBroadcast(intent);
                break;
        }
    }

    void RestoreState () {
        if (settings == null) settings = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        mMode = settings.getInt(PREFS_MODE,0);
        lat = settings.getFloat(PREFS_LATITUDE,(float)51.685323);
        lng = settings.getFloat(PREFS_LONGITUDE,(float)39.172993);
    }

    void SaveState () {
        if (settings == null) settings = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(PREFS_MODE,mMode);
        editor.apply();
    }

    void UpdateBacklight () {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Intent locationIntent = new Intent (ACTION);
            locationIntent.setClassName(getPackageName(),getClass().getName());
            locationIntent.putExtra(CMD,CMD_LOCATION_UPDATE);
            ((LocationManager) getSystemService(LOCATION_SERVICE)).requestSingleUpdate(LocationManager.GPS_PROVIDER,PendingIntent.getService(this,CMD_LOCATION_UPDATE,locationIntent,0));
        }

        BACKLIGHT_INDEX index = BACKLIGHT_INDEX.BACKLIGHT_INDEX_NIGHT;
        int brightness;
        Calendar calendar = Calendar.getInstance();
        SunCalc suncalc = new SunCalc();

        long time = calendar.getTimeInMillis();
        suncalc.getTimes(time , lat, lng);

        if ((time >= suncalc.getTime(SunCalc.SUNCALC_TIME.SUNCALC_DAWN_DUSK).riseTime)
                && (time < suncalc.getTime(SunCalc.SUNCALC_TIME.SUNCALC_SUNRISE_SUNSET).riseTime))
            index = BACKLIGHT_INDEX.BACKLIGHT_INDEX_DAWN;

        if ((time >= suncalc.getTime(SunCalc.SUNCALC_TIME.SUNCALC_SUNRISE_SUNSET).riseTime)
                && (time < suncalc.getTime(SunCalc.SUNCALC_TIME.SUNCALC_SUNRISE_END_SUNSET_START).riseTime))
            index = BACKLIGHT_INDEX.BACKLIGHT_INDEX_SUNRISE;

        if ((time >= suncalc.getTime(SunCalc.SUNCALC_TIME.SUNCALC_SUNRISE_END_SUNSET_START).riseTime)
                && (time < suncalc.getTime(SunCalc.SUNCALC_TIME.SUNCALC_SUNRISE_END_SUNSET_START).setTime))
            index = BACKLIGHT_INDEX.BACKLIGHT_INDEX_DAY;

        if ((time >= suncalc.getTime(SunCalc.SUNCALC_TIME.SUNCALC_SUNRISE_END_SUNSET_START).setTime)
                && (time < suncalc.getTime(SunCalc.SUNCALC_TIME.SUNCALC_SUNRISE_SUNSET).setTime))
            index = BACKLIGHT_INDEX.BACKLIGHT_INDEX_SUNSET;

        if ((time >= suncalc.getTime(SunCalc.SUNCALC_TIME.SUNCALC_SUNRISE_SUNSET).setTime)
                && (time < suncalc.getTime(SunCalc.SUNCALC_TIME.SUNCALC_DAWN_DUSK).setTime))
            index = BACKLIGHT_INDEX.BACKLIGHT_INDEX_DUSK;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        switch (index) {
            case BACKLIGHT_INDEX_DAWN:
                brightness = sharedPref.getInt(getString(R.string.prefBacklightLevelsDawn),getResources().getInteger(R.integer.prefBacklightLevelsDawnDefault));
                break;
            case BACKLIGHT_INDEX_SUNRISE:
                brightness = sharedPref.getInt(getString(R.string.prefBacklightLevelsSunrise),getResources().getInteger(R.integer.prefBacklightLevelsSunriseDefault));
                break;
            case BACKLIGHT_INDEX_DAY:
                brightness = sharedPref.getInt(getString(R.string.prefBacklightLevelsDay),getResources().getInteger(R.integer.prefBacklightLevelsDayDefault));
                break;
            case BACKLIGHT_INDEX_SUNSET:
                brightness = sharedPref.getInt(getString(R.string.prefBacklightLevelsSunset),getResources().getInteger(R.integer.prefBacklightLevelsSunsetDefault));
                break;
            case BACKLIGHT_INDEX_DUSK:
                brightness = sharedPref.getInt(getString(R.string.prefBacklightLevelsDusk),getResources().getInteger(R.integer.prefBacklightLevelsDuskDefault));
                break;
            default:
                brightness = sharedPref.getInt(getString(R.string.prefBacklightLevelsNight),getResources().getInteger(R.integer.prefBacklightLevelsNightDefault));
                break;
        }

        Intent intent = new Intent (C500Helper.BROADCAST_LANCHER_FUNC_BRIGHTNESS_LEVEL);
        intent.putExtra(C500Helper.BROADCAST_LANCHER_FUNC_BRIGHTNESS_LEVEL_EXTRA,brightness);
        sendBroadcast(intent);
    }
}
