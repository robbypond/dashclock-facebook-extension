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

import com.brennasoft.facebookdashclockextension.fbclient.InboxResponse;
import com.brennasoft.facebookdashclockextension.fbclient.NotificationsResponse;

import java.util.Date;

public class BodyBuilder extends UpdateBuilder {

    public BodyBuilder(NotificationsResponse notificationsResponse, InboxResponse inboxResponse) {
        super(notificationsResponse, inboxResponse);
    }

    public String build() {
        String body = "";
        if(validNotificationsResponse() && validInboxResponse() && notificationResponse.count > 0 && inboxResponse.count > 0) {
            Date notificationDate = notificationResponse.getNotificationDate();
            Date messageDate = inboxResponse.time;
            body = notificationDate.getTime() > messageDate.getTime() ? notificationResponse.getNotificationText() : inboxResponse.getText();
        } else if(validNotificationsResponse() && notificationResponse.count > 0) {
            body = notificationResponse.getNotificationText();
        } else if(validInboxResponse()) {
            body = inboxResponse.getText();
        }
        return body;
    }
}
