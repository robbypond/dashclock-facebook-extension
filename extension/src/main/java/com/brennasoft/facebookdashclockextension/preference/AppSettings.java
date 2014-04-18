/*
 * Copyright 2014, Robby Pond
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.brennasoft.facebookdashclockextension.preference;

import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public final class AppSettings {

    public static final String PREF_KEY_APP = "pref_key_app";
    public static final String PREF_KEY_APP_COMPONENT_NAME = "pref_key_app_component_name";
    public static final String PREF_KEY_NAME = "pref_key_name";
    public static final String PREF_KEY_LOGGED_IN = "pref_key_logged_in";

    private final SharedPreferences prefs;

    public AppSettings(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    public boolean getUpdateWhenScreenOn() {
        return prefs.getBoolean("pref_key_update_on_screen_on", false);
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(PREF_KEY_LOGGED_IN, false);
    }

    public boolean getShowNotifications() {
        Set<String> notificationTypes = getNotificationTypes();
        return notificationTypes.contains("1");
    }

    private Set<String> getNotificationTypes() {
        return prefs.getStringSet("pref_key_notification_types", new HashSet<String>());
    }

    public boolean getFilterNotifications() {
        return prefs.getBoolean("pref_key_filter_notifications", false);
    }

    public Set<String> getApplicationTypes() {
        return prefs.getStringSet("pref_key_notification_applications", new HashSet<String>());
    }

    public boolean getShowMessages() {
        Set<String> notificationTypes = getNotificationTypes();
        return notificationTypes.contains("0");
    }

    public boolean getShowAlways() {
        return prefs.getBoolean("pref_key_show_always", false);
    }

    public boolean getShowCondensed() {
        return (Integer.parseInt(prefs.getString("pref_key_display_style", "0")) == 1);
    }

    public boolean getShowPreview() {
        return prefs.getBoolean("pref_key_include_preview", true);
    }

    public boolean getUseMobileSite() {
        return prefs.getBoolean("pref_key_mobile_site", true);
    }

    public boolean getGoToNotificationsPage() {
        return prefs.getBoolean("pref_key_website_notifications", false);
    }

    public String getComponentName() {
        return prefs.getString("pref_key_app_component_name", "");
    }

    public boolean getLaunchMessengerOnMessage() {
        return prefs.getBoolean("pref_key_launch_messenger_on_message", false);
    }

    public boolean getClearNotifications() {
        return prefs.getBoolean("pref_key_clear_notifications", false);
    }
}
