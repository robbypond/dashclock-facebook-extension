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

package com.brennasoft.facebookdashclockextension.ui;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;

import com.brennasoft.facebookdashclockextension.R;
import com.brennasoft.facebookdashclockextension.preference.SharedPreferenceSaver;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.UserSettingsFragment;

import java.util.Arrays;

public class LoginActivity extends FragmentActivity implements StatusCallback {

	private SharedPreferences mPreferences;
	private SharedPreferenceSaver mSharedPreferenceSaver;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

        UserSettingsFragment fragment = (UserSettingsFragment) getSupportFragmentManager().findFragmentById(R.id.facebook_settings_fragment);
        fragment.setReadPermissions(Arrays.asList("read_mailbox"));
        fragment.setSessionStatusCallback(this);
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		
		mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		mSharedPreferenceSaver = new SharedPreferenceSaver(this);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	@Override
	public void call(final Session session, SessionState state, Exception exception) {
		final Editor e = mPreferences.edit();
		final String nameKey = "pref_key_name";
		final String loggedInKey = "pref_key_logged_in";
		if(state.isOpened()) {
			// make request to the /me API
            Request.newMeRequest(session, new Request.GraphUserCallback() {
                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if(user != null) {
                        e.putString(nameKey, user.getName());
                        e.putBoolean(loggedInKey, true);
                        mSharedPreferenceSaver.savePreferences(e, true);
                        requestNotificationAccess(session);
                        setResult(SettingsActivity.FACEBOOK_LOGIN_COMPLETE);
                    } else {
                        setResult(SettingsActivity.FACEBOOK_LOGIN_FAILED);
                    }
                }
            }).executeAsync();
		} else if(state.isClosed()) {
			e.putString(nameKey, getString(R.string.not_logged_in));
			e.putBoolean(loggedInKey, false);
			mSharedPreferenceSaver.savePreferences(e, true);
			setResult(SettingsActivity.FACEBOOK_LOGIN_FAILED);
		}
	}

	void requestNotificationAccess(Session session) {
		session.requestNewPublishPermissions(
                new Session.NewPermissionsRequest(this, Arrays.asList("manage_notifications")));
	}
}
