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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.brennasoft.facebookdashclockextension.FacebookDashService;
import com.crashlytics.android.Crashlytics;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Session;

import java.net.URISyntaxException;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public final class ClearActivity extends Activity {

    public static final String ARG_IDS = "ids";

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        final List<String> ids = getIntent().getStringArrayListExtra(ARG_IDS);

        //this is pretty gross
        if(ids != null && ids.size() > 0) {
            Observable.create(new Observable.OnSubscribe<Void>() {
                @Override
                public void call(Subscriber<? super Void> subscriber) {
                    Session session = Session.openActiveSessionFromCache(getApplicationContext());
                    if (session != null && session.isOpened()) {
                        Bundle args = new Bundle();
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0, j = ids.size(); i < j; i++) {
                            sb.append(ids.get(i));
                            if (i < (j - 1)) {
                                sb.append(",");
                            }
                        }
                        args.putString("notification_ids", sb.toString());
                        Request req = new Request();
                        req.setSession(session);
                        req.setRestMethod("notifications.markRead");
                        req.setHttpMethod(HttpMethod.POST);
                        req.setParameters(args);
                        req.executeAndWait();
                    }

                }
            }).subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {

                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Crashlytics.logException(throwable);
                        }
                    });
        }

        String uri = getIntent().getStringExtra(FacebookDashService.LAUNCH_INTENT);
        if(!TextUtils.isEmpty(uri)) {
            try {
                Intent intent = Intent.parseUri(uri, Intent.URI_INTENT_SCHEME);
                startActivity(intent);
            } catch (URISyntaxException e) {
                Crashlytics.logException(e);
            }
        }
	}
}
