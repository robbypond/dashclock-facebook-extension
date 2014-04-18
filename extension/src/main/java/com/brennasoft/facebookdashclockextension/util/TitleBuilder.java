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

package com.brennasoft.facebookdashclockextension.util;

import android.content.res.Resources;

import com.brennasoft.facebookdashclockextension.R;
import com.brennasoft.facebookdashclockextension.fbclient.InboxResponse;
import com.brennasoft.facebookdashclockextension.fbclient.NotificationsResponse;

public class TitleBuilder extends UpdateBuilder {

    private final Resources resources;

    public TitleBuilder(NotificationsResponse notificationsResponse, InboxResponse inboxResponse, Resources resources) {
        super(notificationsResponse, inboxResponse);
        this.resources = resources;
    }

    public String buildCondensed() {
        String title = "";
        if(validNotificationsResponse() && validInboxResponse()) {
            title = resources.getString(R.string.message_condensed, inboxResponse.count);
            title += " ";
            title += resources.getString(R.string.updates_condensed, notificationResponse.count);
        } else if(validNotificationsResponse()) {
            title = resources.getString(R.string.updates_condensed, notificationResponse.count);
        } else if(validInboxResponse()) {
            title = resources.getString(R.string.message_condensed, inboxResponse.count);
        }
        return title;
    }

    public String build() {
        String title = "";
        if(validNotificationsResponse() && validInboxResponse()) {
            title = resources.getQuantityString(R.plurals.message, inboxResponse.count, inboxResponse.count);
            title += " / ";
            title += resources.getQuantityString(R.plurals.notification, notificationResponse.count, notificationResponse.count);
        } else if(validNotificationsResponse()) {
            title = resources.getQuantityString(R.plurals.notification, notificationResponse.count, notificationResponse.count);
        } else if(validInboxResponse()) {
            title = resources.getQuantityString(R.plurals.message, inboxResponse.count, inboxResponse.count);
        }
        return title;
    }
}
