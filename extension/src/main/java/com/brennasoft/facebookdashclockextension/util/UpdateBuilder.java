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

public abstract class UpdateBuilder {
    protected final NotificationsResponse notificationResponse;
    protected final InboxResponse inboxResponse;

    public UpdateBuilder(NotificationsResponse notificationsResponse, InboxResponse inboxResponse) {
        this.notificationResponse = notificationsResponse;
        this.inboxResponse = inboxResponse;
    }

    protected boolean validInboxResponse() {
        return inboxResponse != null && inboxResponse.success;
    }

    protected boolean validNotificationsResponse() {
        return notificationResponse != null && notificationResponse.success;
    }
}
