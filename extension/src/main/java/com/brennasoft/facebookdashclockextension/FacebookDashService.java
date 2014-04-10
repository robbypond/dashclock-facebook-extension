package com.brennasoft.facebookdashclockextension;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.brennasoft.facebookdashclockextension.ui.ClearActivity;
import com.brennasoft.facebookdashclockextension.util.AppUtils;
import com.crashlytics.android.Crashlytics;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class FacebookDashService extends DashClockExtension {

    private static final String messageFields = "id,unread";
    private static final SimpleDateFormat facebookDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private static final String notificationFql = "select title_text, created_time from notification where recipient_id = me() and is_unread = 1";

	private static final SimpleDateFormat mSdf = new SimpleDateFormat("yyyyMMdd");

	public static final String LAUNCH_INTENT = "LAUNCH_INTENT";
	
	private Set<String> mNotificationTypes, mApplicationTypes;
	private boolean mLoggedIn, mShowAlways, mMobileSite,mShowCondensed,mIncludePreview,mClearNotifications, mLaunchMessengerOnMessage, mFilterNotifications, mGoToNotificationsPage,
            mShowNotifications = false, mShowMessages = false;
    private String mComponentName;

    @Override
    protected void onInitialize(boolean isReconnect) {
        super.onInitialize(isReconnect);
        loadPrefs();
    }

    @Override
	protected void onUpdateData(int reason) {
        if(reason == DashClockExtension.UPDATE_REASON_SETTINGS_CHANGED) {
            loadPrefs();
        }
		if(isOnline()) {
			publishUpdate(createExtensionData());
		}
	}

	private void loadPrefs() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
		mLoggedIn = prefs.getBoolean(getString(R.string.pref_key_logged_in), false);
		mShowAlways = prefs.getBoolean(getString(R.string.pref_key_show_always), false);
		mComponentName = prefs.getString(getString(R.string.pref_key_app_component_name), "");
		mMobileSite = prefs.getBoolean(getString(R.string.pref_key_mobile_site), true);
		mIncludePreview = prefs.getBoolean(getString(R.string.pref_key_include_preview), true);
		mClearNotifications = prefs.getBoolean(getString(R.string.pref_key_clear_notifications), false);
		mLaunchMessengerOnMessage = prefs.getBoolean(getString(R.string.pref_key_launch_messenger_on_message), false);
        mApplicationTypes = prefs.getStringSet(getString(R.string.pref_key_notification_applications), new HashSet<String>());
        mFilterNotifications = prefs.getBoolean(getString(R.string.pref_key_filter_notifications), false);
        mGoToNotificationsPage = prefs.getBoolean(getString(R.string.pref_key_website_notifications), false);
        mShowCondensed = (Integer.parseInt(prefs.getString(getString(R.string.pref_key_display_style), "0")) == 1);

        Set<String> notificationTypes = prefs.getStringSet(getString(R.string.pref_key_notification_types), new HashSet<String>());
        mShowMessages = notificationTypes.contains("0");
        mShowNotifications = notificationTypes.contains("1");

        boolean updateWhenScreenOn = prefs.getBoolean(getString(R.string.pref_key_update_on_screen_on), false);
        setUpdateWhenScreenOn(updateWhenScreenOn);
	}

	private ExtensionData createExtensionData() {
		ExtensionData data = new ExtensionData().icon(R.drawable.ic_extension);
        if(mLoggedIn && isOnline()) {
            Session session = Session.openActiveSessionFromCache(this);
            if(session != null && session.isOpened()) {
                NotificationInfo notificationInfo = null;
                MessageInfo messageInfo = null;
                String body = null;
                int messageCount = 0, notificationCount = 0;
                if(mShowNotifications) {
                    if(mFilterNotifications && mApplicationTypes.size() > 0) {
                        notificationInfo = getNotificationInfoFiltered(session);
                    } else {
                        notificationInfo = getNotificationInfo(session);
                    }
                    notificationCount = notificationInfo.count;
                    if(notificationCount > 0) {
                        body = notificationInfo.body;
                    }
                }
                if(mShowMessages) {
                    messageInfo = getMessageInfo(session);
                    messageCount = messageInfo.count;
                    if(messageCount > 0) {
                        body = messageInfo.latestBody;
                    }
                }
                Resources res = getResources();
                if(mShowAlways || (messageCount + notificationCount > 0)) {
                    String title = "", status;
                    if(mShowMessages && mShowNotifications) {
                        if(!mShowCondensed) {
                            status = messageCount + "/" + notificationCount;
                            title = res.getQuantityString(R.plurals.message, messageCount, messageCount);
                            title += " / ";
                            title += res.getQuantityString(R.plurals.notification, notificationCount, notificationCount);
                        } else {
                            status = messageCount + notificationCount + "";
                            if(messageCount > 0) {
                                title += res.getString(R.string.message_condensed, messageCount);
                            }
                            if(notificationCount > 0) {
                                title += " " + res.getString(R.string.updates_condensed, notificationCount);
                            }
                        }
                    } else if(mShowMessages) {
                        status = messageCount + "";
                        if(mShowCondensed && messageCount > 0) {
                            title = res.getString(R.string.message_condensed, messageCount);
                        } else {
                            title = res.getQuantityString(R.plurals.message, messageCount, messageCount);
                        }
                    } else {
                        status = notificationCount + "";
                        if(mShowCondensed && notificationCount > 0) {
                            title = res.getString(R.string.updates_condensed, notificationCount);
                        } else {
                            title = res.getQuantityString(R.plurals.notification, notificationCount, notificationCount);
                        }
                    }
                    data.status(status).expandedTitle(title.trim());
                    if(mIncludePreview && !TextUtils.isEmpty(body)) {
                        data.expandedBody(body);
                    }
                }
                Intent theIntent = getIntent(messageCount);
                data.clickIntent(theIntent);
                data.visible(!TextUtils.isEmpty(data.status()));
                Date sessionExpires = session.getExpirationDate();
                Date now = new Date();
                if(sessionExpires != null && mSdf.format(sessionExpires).equals(mSdf.format(now))) {
                    data.expandedTitle(getString(R.string.expires));
                    data.visible(true);
                }
            } else {
                data.expandedTitle(getString(R.string.not_logged_in));
                data.visible(true);
            }
        } else {
            data.expandedTitle(getString(R.string.not_logged_in));
            data.visible(true);
        }
		return data;
	}

    private Intent getIntent(int messageCount) {
        Intent theIntent;Uri siteUri = Uri.parse(mMobileSite ? "http://m.facebook.com" : "http://www.facebook.com" + (mGoToNotificationsPage ? "/notifications" : ""));
        if(TextUtils.isEmpty(mComponentName)) { // try facebook default
            if(AppUtils.isIntentAvailable(this, new Intent(Intent.ACTION_VIEW, Uri.parse("facebook://notifications")))) {
                theIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("facebook://notifications"));
            } else { // no facebook default to browser
                theIntent = new Intent(Intent.ACTION_VIEW);
            }
        } else { // component is set
            ComponentName comp = ComponentName.unflattenFromString(mComponentName);
            Intent intent = Intent.makeMainActivity(comp);
            if(AppUtils.isIntentAvailable(this, intent)) { // check if it still exists
                theIntent = intent;
            } else {
                theIntent = new Intent(Intent.ACTION_VIEW, siteUri);
            }
        }
        if(AppUtils.getIntent(theIntent.getPackage()) == null) {
            theIntent.setData(siteUri);
        }

        if(messageCount > 0 && mLaunchMessengerOnMessage && AppUtils.hasMessenger(this)) {
            theIntent = new Intent();
            theIntent.setComponent(new ComponentName("com.facebook.orca", "com.facebook.orca.auth.StartScreenActivity"));
        }

        if(mClearNotifications) {
            String launchUri = theIntent.toUri(Intent.URI_INTENT_SCHEME);
            theIntent = new Intent(this, ClearActivity.class);
            theIntent.putExtra(FacebookDashService.LAUNCH_INTENT, launchUri);
        }
        return theIntent;
    }

    private NotificationInfo getNotificationInfo(Session session) {
        Request request = new Request(session, "me/notifications");
        Bundle parameters = new Bundle();
        if(mIncludePreview) {
            parameters.putString("fields", "title");
        }
        request.setParameters(parameters);
        parameters.putInt("limit", 1);
        Response resp = request.executeAndWait();
        NotificationInfo notificationInfo = new NotificationInfo();
        if(resp.getError() == null) {
            JSONObject root = resp.getGraphObject().getInnerJSONObject();
            try {
                if(root.has("summary") && root.get("summary") instanceof JSONObject) {
                    JSONObject summary = root.getJSONObject("summary");
                    notificationInfo.count = summary.getInt("unseen_count");
                    if(notificationInfo.count  > 0) {
                        JSONArray jsonArray = root.getJSONArray("data");
                        if(jsonArray.length() > 0) {
                            JSONObject note = jsonArray.getJSONObject(0);
                            notificationInfo.body = note.getString("title");
                            notificationInfo.createdTime = facebookDateFormat.parse(note.getString("created_time"));
                        }
                    }
                }
            } catch (JSONException e) {
                notificationInfo.error = true;
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return notificationInfo;
    }

    NotificationInfo getNotificationInfoFiltered(Session session) {
        Bundle params = new Bundle();
        params.putString("q", notificationFql + " and app_id in " + mApplicationTypes.toString().replace('[', '(').replace(']', ')'));
        Request request = new Request(session,  "/fql",
                params,
                HttpMethod.GET);
        Response response = request.executeAndWait();
        NotificationInfo notificationInfo = new NotificationInfo();
        if(response.getError() == null) {
            try {
                JSONArray data = response.getGraphObject().getInnerJSONObject().getJSONArray("data");
                if(data.length() > 0) {
                    JSONObject note = data.getJSONObject(0);
                    notificationInfo.body = note.getString("title_text");
                    notificationInfo.createdTime = new Date(Long.parseLong(note.getString("created_time")));
                }
                notificationInfo.count = data.length();
            } catch (JSONException e) {
                notificationInfo.error = true;
                Crashlytics.logException(e);
            }
        } else {
            notificationInfo.error = true;
            Crashlytics.log("Error getting messages: " + response.getError().getErrorMessage());
            Crashlytics.logException(response.getError().getException());
        }
        return notificationInfo;
    }

    MessageInfo getMessageInfo(Session session) {
        Request request = new Request(session, "me/inbox");
        Bundle parameters = new Bundle();
        if(mIncludePreview) {
            parameters.putString("fields", messageFields + ",comments");
        }
        request.setParameters(parameters);
        parameters.putInt("limit", 1);
        Response resp = request.executeAndWait();
        MessageInfo msgInfo = new MessageInfo();
        if(resp.getError() == null) {
            JSONObject root = resp.getGraphObject().getInnerJSONObject();
            try {
                JSONObject summary = root.getJSONObject("summary");
                msgInfo.count = summary.getInt("unread_count");
                if(msgInfo.count > 0 && mIncludePreview) {
                    JSONArray messages = root.getJSONArray("data");
                    for(int i=0; i<messages.length(); i++) {
                        JSONObject message = messages.getJSONObject(i);
                        boolean unread = (message.getInt("unread") == 1);
                        if(unread) {
                            JSONArray comments = message.getJSONObject("comments").getJSONArray("data");
                            JSONObject latest = comments.getJSONObject(comments.length() - 1);
                            msgInfo.latestBody = latest.getJSONObject("from").getString("name") + ": " + latest.getString("message");
                            msgInfo.createdTime = facebookDateFormat.parse(latest.getString("created_time"));
                            break;
                        }
                    }
                }
            } catch (JSONException e) {
                msgInfo.error = true;
                Crashlytics.logException(e);
            } catch (ParseException e) {
                Crashlytics.logException(e);
            }
        } else {
            Crashlytics.log("Error getting messages: " + resp.getError().getErrorMessage());
            Crashlytics.logException(resp.getError().getException());
            msgInfo.error = true;
        }
        return msgInfo;
    }

    boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    class NotificationInfo {
        Date createdTime;
        String body;
        int count;
        boolean error;
    }

    public class MessageInfo {
        public Date createdTime;
        public String latestBody;
        public int count;
        public boolean error;
    }
}