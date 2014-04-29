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

import java.util.Date;

public final class InboxRequest {
    private static final String q = "{\"message\":\"select snippet, snippet_author, updated_time " +
            "from thread where unseen = 1 and viewer_id = me() and folder_id = 0\", " +
            "\"user\":\"select name from user where uid in (SELECT snippet_author FROM " +
            "#message)\"}";

    public InboxResponse execute(Session session) {
        InboxResponse inboxResponse = new InboxResponse();
        if (session.isOpened() && session.getPermissions().contains("read_mailbox")) {
            Bundle parameters = new Bundle();
            parameters.putString("q", q);
            Request request = new Request(session, "/fql", parameters, HttpMethod.GET);
            Response response = request.executeAndWait();
            FacebookRequestError error = response.getError();
            if(error == null) {
                inboxResponse = parseResponse(response);
            }
        }
        return inboxResponse;
    }

    private InboxResponse parseResponse(Response response) {
        InboxResponse inboxResponse = new InboxResponse();
        GraphObject graphObject = response.getGraphObject();
        try {
            JSONArray data = graphObject.getInnerJSONObject().getJSONArray("data");
            if(data.length() == 2) {
                JSONArray messageResultSet = data.getJSONObject(0).getJSONArray("fql_result_set");
                JSONArray userResultSet = data.getJSONObject(1).getJSONArray("fql_result_set");
                if(messageResultSet.length() >= 1 && userResultSet.length() >= 1) {
                    JSONObject message = messageResultSet.getJSONObject(0);
                    String snippet = message.getString("snippet");
                    Date time = new Date(message.getLong("updated_time") * 1000);
                    String author = userResultSet.getJSONObject(0).getString("name");
                    inboxResponse.snippet = snippet;
                    inboxResponse.author = author;
                    inboxResponse.time = time;
                    inboxResponse.count = messageResultSet.length();
                    inboxResponse.success = true;
                }
            }
        } catch (JSONException e) {
            Crashlytics.logException(e);
        }
        return inboxResponse;
    }
}
