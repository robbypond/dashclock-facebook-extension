package com.brennasoft.facebookdashclockextension.preference;

import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class AppSettings {

    private SharedPreferences prefs;

    public AppSettings(SharedPreferences prefs) {
        this.prefs = prefs;
    }

    public boolean getUpdateWhenScreenOn() {
        return prefs.getBoolean("pref_key_update_on_screen_on", false);
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean("pref_key_logged_in", false);
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
