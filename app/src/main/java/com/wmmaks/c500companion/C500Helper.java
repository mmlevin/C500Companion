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

public class C500Helper {

    public enum C500_MODES {
        C500_UNINSTALL,
        C500_NAVI,
        C500_MUSIC,
        C500_VIDEO,
        C500_DVD,
        C500_DVDBOX,
        C500_ADAS,
        C500_AVIN,
        C500_BACKCARMAIN,
        C500_RIGHT_CAMERA,
        C500_AUX,
        C500_RADIO,
        C500_BACKCAR,
        C500_CMMB,
        C500_ATV,
        C500_DVR,
        C500_FRONT_CAMERA,
        C500_IPOD,
        C500_TSDAILER,
        C500_VIEW360,
        C500_SETTINGS,
        C500_CARINFO,
        C500_BT_CONNECT,
        C500_BT_MUSIC,
        C500_CAN_CAR_DEVICE,
        C500_CAN_EXRADIO,
        C500_CAN_EXCD,
        C500_CAN_FORD,
        C500_TPMS,
        C500_TPMS_SETTINGS,
        C500_OBD,
        C500_WEATHER
    };

    public static final String ACTION_MAINUI_ACCOFF = "com.ts.main.uiaccoff";

    // General broadcast action
    public static final String ACTION_RECOGNIZE_CMD = "com.globalconstant.BROADCAST_SEND_CMD";
    // Device domain for command
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN = "domain";

    // MUSIC mode domain
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_MUSIC = "music";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_MUSIC_ACTION = "action";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_MUSIC_ACTION_OPEN = "open";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_MUSIC_ACTION_RANDOM = "random";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_MUSIC_ACTION_NEXT = "next";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_MUSIC_ACTION_PREV = "prev";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_MUSIC_ACTION_RESUME = "resume";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_MUSIC_ACTION_PAUSE = "pause";

    // APP domain
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_APP = "app";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_APP_ACTION = "action";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_APP_ACTION_OPEN = "open";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_APP_EXTRA_PACKAGE = "pck";

    // Volume control domain
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_VOLUME = "volume";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_VOLUME_ACTION = "action";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_VOLUME_ACTION_UP = "up";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_VOLUME_ACTION_DOWN = "down";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_VOLUME_ACTION_MAX = "max";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_VOLUME_ACTION_MIN = "min";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_VOLUME_ACTION_MUTE_ON = "mute_on";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_VOLUME_ACTION_MUTE_OFF = "mute_off";

    // Radio control domain
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_RADIO = "radio";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_RADIO_ACTION = "action";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_RADIO_FORWARD = "forword";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_RADIO_BACKWARD = "back";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_RADIO_PREV = "prev";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_RADIO_NEXT = "next";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_RADIO_BAND = "channelStyle";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_RADIO_BAND_FM = "FM";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_RADIO_BAND_AM = "AM";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_RADIO_FREQ = "channelValue";

    // Navigation control domain
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_NAVIGATION = "navigation";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_NAVIGATION_ACTION = "action";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_NAVIGATION_ACTION_OPEN = "open";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_NAVIGATION_ACTION_CLOSE = "close";

    // Device control domain
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE = "device";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_ACTION = "action";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_ACTION_OPEN = "open";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_ACTION_CLOSE = "close";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME = "device_name";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME_OPTICAL_DISC_HYEROGLYPH = "\u5149\u76d8";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME_BLUETOOTH_HYEROGLYPH = "\u84dd\u7259";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME_RADIO_HYEROGLYPH = "\u6536\u97f3\u673a";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME_VIDEO_HYEROGLYPH = "\u89c6\u9891";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME_IPOD = "IPOD";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME_BLUETOOTH = "bluetooth";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME_PHONE = "phone";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME_RADIO = "radio";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME_MOVIE = "movie";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME_MUSIC = "music";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME_DEVICE_SETTINGS = "device_setting";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME_AVIN = "avin";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_DRV = "driving_record";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME_TPMS = "TPMS";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME_ADAS = "driving_assistance";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME_CAN = "driving_message";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME_DVD = "dvd";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_DEVICE_NAME_TV = "tv";

    // Device control Autoking domain
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_AUTOKING = "autoking";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_AUTOKING_ACTION = "action";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_AUTOKING_ACTION_FACTORY_RESET = "factory_reset";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_AUTOKING_ACTION_HOMEKEY_ENABLE = "homekey_enable";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_AUTOKING_ACTION_HOMEKEY_DISABLE = "homekey_disable";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_AUTOKING_ACTION_READY_FOR_ACC_OFF = "ready_for_acc_off";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_AUTOKING_ACTION_REBOOT = "reboot_system";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_AUTOKING_ACTION_REQUEST_VR_STATUS = "request_vr_status";

    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_PHONE = "PHONE";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_PHONE_ACTION = "action";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_PHONE_ACTION_CALL = "call";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_PHONE_ACTION_CALL_EXTRA_NUMBER = "number";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_PHONE_ACTION_INCOMING_CALL = "incomingcall";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_PHONE_ACTION_INCOMING_CALL_EXTRA_ANSWER = "answerIntent";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_PHONE_ACTION_INCOMING_CALL_EXTRA_ANSWER_REJECT = "reject";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_PHONE_ACTION_INCOMING_CALL_EXTRA_ANSWER_ACCEPT = "accept";
    public static final String ACTION_RECOGNIZE_CMD_DOMAIN_PHONE_ACTION_REQUEST_PHONEBOOK = "requestphoneList";


    public static final String ACTION_RECOGNIZE_CMD_B = "com.globalconstant.BROADCAST_CAR_SEND_CMD";
    public static final String ACTION_RECOGNIZE_CMD_MIC_CLICK = "com.globalconstant.BROADCAST_MIC_CLICK";

    // Request register status into Log.i
    public static final String ACTION_RECOGNIZE_CMD_REGISTER_S = "com.globalconstant.BROADCAST_register_status";
    public static final String ACTION_RECOGNIZE_CMD_REGISTER_S_EXTRA = "register_status";

    // Volume command
    public static final String ACTION_RECOGNIZE_CMD_VOLUME = "com.globalconstant.BROADCAST_SEND_VOLUME";
    public static final String ACTION_RECOGNIZE_CMD_VOLUME_EXTRA = "mute";
    public static final int ACTION_RECOGNIZE_CMD_VOLUME_EXTRA_SETMUTE = 1;
    public static final int ACTION_RECOGNIZE_CMD_VOLUME_EXTRA_CLEARMUTE = 2;

    public static final String ACTION_RECOGNIZE_TTSINFO_CMD = "com.globalconstant.BROADCAST_TTS_INFO";
    public static final String BASIC_TIME_STRING = "2016-10-01 00:00:00";
    public static final String BROADCAST_ADAS_LIGHT_STATE = "forfan.adas.light_state";
    public static final String BROADCAST_ADAS_TURNLIGHT_STATE = "forfan.adas.turnlight_state";

    // Probably not used
    public static final String BROADCAST_APP_QUIT = "net.easyconn.app.quit";

    // Enter to A2DP mode
    public static final String BROADCAST_BT_A2DP_ACQUIRE = "net.easyconn.a2dp.acquire";
    // Probably not used
    public static final String BROADCAST_BT_A2DP_RELEASE = "net.easyconn.a2dp.release";

    // Check BT connection status
    public static final String BROADCAST_BT_CHECKSTATUS_ACTION = "net.easyconn.bt.checkstatus";
    public static final String BROADCAST_BT_CONNECTED_ANSWER = "net.easyconn.bt.connected";
    public static final String BROADCAST_BT_OPENED_ANSWER = "net.easyconn.bt.opened@name=XXXX@pin=XXXX";

    /* Probably not used
    public static final String BROADCAST_BT_CONNECT = "net.easyconn.bt.connect";
    public static final String BROADCAST_BT_NOT_CONNECTED = "net.easyconn.bt.notconnected";
    public static final String BROADCAST_BT_OPENED = "net.easyconn.bt.opened";
    public static final String BROADCAST_CAMERA_SWITCH_STRING = "forfan.camera.switch";
    public static final String BROADCAST_CONNECTION_BREAK = "net.easyconn.connection.break";
    */

    // Start voice recognition
    public static final String BROADCAST_GLSX_VOICE = "com.glsx.ddbox.action.VOICE";

    // Probably not used
    public static final String BROADCAST_LANCHER_FUNC_ACCELERATION = "forfan.intent.action.ACCELERATION";

    // Bluetooth
    public static final String BROADCAST_LANCHER_FUNC_BLUETOOTH = "forfan.intent.action.BLUETOOTH";

    // Display settings
    public static final String BROADCAST_LANCHER_FUNC_BRIGHTNESS = "forfan.intent.action.BRIGHTNESS";
    public static final String BROADCAST_LANCHER_FUNC_BRIGHTNESS_LEVEL = "forfan.intent.action.BRIGHT_LEVEL";
    public static final String BROADCAST_LANCHER_FUNC_BRIGHTNESS_LEVEL_EXTRA = "brightness";

    // Mute
    public static final String BROADCAST_LANCHER_FUNC_MUTE = "forfan.intent.action.MUTE";

    // Screen off
    public static final String BROADCAST_LANCHER_FUNC_SCREENOFF = "forfan.intent.action.SCREENOFF";

    // Show volume bar
    public static final String BROADCAST_LANCHER_FUNC_VOLUME = "forfan.intent.action.VOLUME";

    // Increment volume
    public static final String BROADCAST_LANCHER_FUNC_VOLUMEADD = "forfan.intent.action.VOLUMEUP";

    // Decrement volume
    public static final String BROADCAST_LANCHER_FUNC_VOLUMEDEC = "forfan.intent.action.VOLUMEDN";

    // Update media list
    public static final String BROADCAST_MEDIA_LISTUPDATE = "com.ts.media.listupdate";

    // Receive network change status
    public static final String BROADCAST_NET_CHANGE = "android.net.conn.CONNECTIVITY_CHANGE";

    // Get
    public static final String TXZ_GET_WWATHER_DATA_ACTION = "txz_get_weather_data";
    public static final String TXZ_GET_WWATHER_DATA_EXTRA = "City";
}

