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

package com.brennasoft.facebookdashclockextension;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.brennasoft.facebookdashclockextension.fbclient.InboxRequest;
import com.brennasoft.facebookdashclockextension.fbclient.InboxResponse;
import com.brennasoft.facebookdashclockextension.fbclient.NotificationsRequest;
import com.brennasoft.facebookdashclockextension.fbclient.NotificationsResponse;
import com.brennasoft.facebookdashclockextension.preference.AppSettings;
import com.brennasoft.facebookdashclockextension.ui.ClearActivity;
import com.brennasoft.facebookdashclockextension.util.AppUtils;
import com.brennasoft.facebookdashclockextension.util.BodyBuilder;
import com.brennasoft.facebookdashclockextension.util.StatusBuilder;
import com.brennasoft.facebookdashclockextension.util.TitleBuilder;
import com.facebook.Session;
import com.google.android.apps.dashclock.api.DashClockExtension;
import com.google.android.apps.dashclock.api.ExtensionData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FacebookDashService extends DashClockExtension {

	public static final String LAUNCH_INTENT = "LAUNCH_INTENT";
	
    private AppSettings mAppSettings;

    private static final SimpleDateFormat mSdf = new SimpleDateFormat("yyyyMMdd");

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
        mAppSettings = new AppSettings(prefs);
        setUpdateWhenScreenOn(mAppSettings.getUpdateWhenScreenOn());
	}

    private ExtensionData createExtensionData() {
        ExtensionData data = new ExtensionData().icon(R.drawable.ic_extension);
        Session session = Session.openActiveSessionFromCache(this);
        if(mAppSettings.isLoggedIn() && session != null && session.isOpened() && isOnline()) {
            NotificationsResponse notificationsResponse = null;
            if(mAppSettings.getShowNotifications()) {
                notificationsResponse = getNotifications(session);
            }
            InboxResponse inboxResponse = null;
            if(mAppSettings.getShowMessages()) {
                inboxResponse = getInbox(session);
            }
            data.status(getStatus(notificationsResponse, inboxResponse));
            data.expandedTitle(getExpandedTitle(notificationsResponse, inboxResponse));
            data.expandedBody(getExpandedBody(notificationsResponse, inboxResponse));
            data.contentDescription(data.expandedBody());
            data.visible(shouldBeVisible(notificationsResponse, inboxResponse));
            Intent theIntent = getIntent(notificationsResponse, inboxResponse);
            data.clickIntent(theIntent);
            Date sessionExpires = session.getExpirationDate();
            Date now = new Date();
            if(sessionExpires != null && mSdf.format(sessionExpires).equals(mSdf.format(now))) {
                data.expandedTitle(getString(R.string.expires));
                data.visible(true);
            }
        }
        return data;
	}

    private boolean shouldBeVisible(NotificationsResponse notificationsResponse, InboxResponse inboxResponse) {
        return mAppSettings.getShowAlways() || ((notificationsResponse != null && notificationsResponse.count > 0) || (inboxResponse != null && inboxResponse.count > 0));
    }

    private String getExpandedBody(NotificationsResponse notificationsResponse, InboxResponse inboxResponse) {
        String body = "";
        if(mAppSettings.getShowPreview()) {
            body = new BodyBuilder(notificationsResponse, inboxResponse).build();
        }
        return body;
    }

    private String getExpandedTitle(NotificationsResponse notificationsResponse, InboxResponse inboxResponse) {
        TitleBuilder titleBuilder = new TitleBuilder(notificationsResponse, inboxResponse, getResources());
        return mAppSettings.getShowCondensed() ? titleBuilder.buildCondensed() : titleBuilder.build();
    }

    private String getStatus(NotificationsResponse notificationsResponse, InboxResponse inboxResponse) {
        StatusBuilder statusBuilder = new StatusBuilder(notificationsResponse, inboxResponse);
        return mAppSettings.getShowCondensed() ? statusBuilder.buildCondensed() : statusBuilder.build();
    }

    private Intent getIntent(NotificationsResponse notificationsResponse, InboxResponse inboxResponse) {
        int messageCount = inboxResponse != null ? inboxResponse.count : 0;
        Intent theIntent;
        Uri siteUri = Uri.parse(mAppSettings.getUseMobileSite() ? "http://m.facebook.com" : "http://www.facebook.com" + (mAppSettings.getGoToNotificationsPage() ? "/notifications" : ""));
        if(TextUtils.isEmpty(mAppSettings.getComponentName())) { // try facebook default
            theIntent = getDefaultIntent();
        } else { // component is set
            theIntent = getIntentFromComponent(siteUri);
        }
        if(AppUtils.getIntent(theIntent.getPackage()) == null) {
            theIntent.setData(siteUri);
        }
        if(messageCount > 0 && mAppSettings.getLaunchMessengerOnMessage() && AppUtils.hasMessenger(this)) {
            theIntent = new Intent();
            theIntent.setComponent(new ComponentName("com.facebook.orca", "com.facebook.orca.auth.StartScreenActivity"));
        }
        if(mAppSettings.getClearNotifications()) {
            theIntent = addClearActivityToIntent(theIntent);
            theIntent = addNotificationIdsToIntent(notificationsResponse ,theIntent);
        }
        return theIntent;
    }

    private Intent addNotificationIdsToIntent(NotificationsResponse notificationsResponse, Intent theIntent) {
        if(notificationsResponse != null && notificationsResponse.count > 0) {
            ArrayList<String> ids = new ArrayList<>();
            for(int i=0; i<notificationsResponse.count; i++) {
                ids.add(notificationsResponse.getNotificationId(i));
            }
            theIntent.putStringArrayListExtra(ClearActivity.ARG_IDS, ids);
        }
        return theIntent;
    }

    private Intent getIntentFromComponent(Uri siteUri) {
        Intent theIntent;ComponentName comp = ComponentName.unflattenFromString(mAppSettings.getComponentName());
        Intent intent = Intent.makeMainActivity(comp);
        if(AppUtils.isIntentAvailable(this, intent)) { // check if it still exists
            theIntent = intent;
        } else {
            theIntent = new Intent(Intent.ACTION_VIEW, siteUri);
        }
        return theIntent;
    }

    private Intent getDefaultIntent() {
        Intent theIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("facebook://notifications"));
        theIntent.setPackage("com.facebook.katana");
        if(!AppUtils.isIntentAvailable(this, theIntent)) {
            theIntent = new Intent(Intent.ACTION_VIEW);
        }
        return theIntent;
    }

    private Intent addClearActivityToIntent(Intent theIntent) {
        String launchUri = theIntent.toUri(Intent.URI_INTENT_SCHEME);
        theIntent = new Intent(this, ClearActivity.class);
        theIntent.putExtra(FacebookDashService.LAUNCH_INTENT, launchUri);
        return theIntent;
    }

    private NotificationsResponse getNotifications(Session session) {
        NotificationsRequest request = new NotificationsRequest();
        NotificationsResponse response;
        if(mAppSettings.getFilterNotifications() && mAppSettings.getApplicationTypes().size() > 0) {
            response = request.executeWithApplicationFilter(session, mAppSettings.getApplicationTypes());
        } else {
            response = request.execute(session);
        }
        return response;
    }


    private InboxResponse getInbox(Session session) {
        InboxRequest request = new InboxRequest();
        return request.execute(session);
    }

    boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}