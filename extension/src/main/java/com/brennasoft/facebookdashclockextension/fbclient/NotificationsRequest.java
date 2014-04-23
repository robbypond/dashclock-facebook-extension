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

package com.brennasoft.facebookdashclockextension.fbclient;

import android.os.Bundle;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

public class NotificationsRequest {

    private static final String q = "select title_text, updated_time, is_unread, notification_id " +
            "from notification where recipient_id = me() and is_unread = 1 LIMIT 100";

    public NotificationsResponse execute(Session session) {
        NotificationsResponse notificationsResponse = new NotificationsResponse();
        if (session.isOpened()) {
            Bundle parameters = new Bundle();
            parameters.putString("q", q);
            Request request = new Request(session, "/fql", parameters, HttpMethod.GET);
            Response response = request.executeAndWait();
            FacebookRequestError error = response.getError();
            if(error == null) {
                notificationsResponse = parseResponse(response);
            }
        }
        return notificationsResponse;
    }

    private NotificationsResponse parseResponse(Response response) {
        NotificationsResponse notificationsResponse = new NotificationsResponse();
        GraphObject graphObject = response.getGraphObject();
        try {
            JSONArray data = graphObject.getInnerJSONObject().getJSONArray("data");
            notificationsResponse.count = data.length();
            for(int i=0; i<data.length(); i++) {
                JSONObject object = data.getJSONObject(i);
                notificationsResponse.addNotification(object.getString("notification_id"),
                        object.getLong("updated_time"), object.getString("title_text"));
            }
            notificationsResponse.success = true;
        } catch (JSONException e) {
            Crashlytics.logException(e);
        }
        return notificationsResponse;
    }

    public NotificationsResponse executeWithApplicationFilter(Session session, Set<String>
            applicationTypes) {
        NotificationsResponse notificationsResponse = null;
        if (session.isOpened()) {
            Bundle parameters = new Bundle();
            parameters.putString("q", q  + " and app_id in " + applicationTypes.toString()
                    .replace('[', '(').replace(']', ')'));
            Request request = new Request(session, "/fql", parameters, HttpMethod.GET);
            Response response = request.executeAndWait();
            FacebookRequestError error = response.getError();
            if(error == null) {
                notificationsResponse = parseResponse(response);
            } else {
                notificationsResponse = new NotificationsResponse();
            }
        }
        return notificationsResponse;
    }
}
