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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NotificationsResponse {
    public boolean success;
    public int count;
    public List<Notification> notifications = new ArrayList<>();

    public void addNotification(String id, long updatedTime, String title) {
        notifications.add(new Notification(id, updatedTime, title));
    }

    public Date getNotificationDate() {
        return notifications.get(0).time;
    }

    public String getNotificationText() {
        return notifications.get(0).text;
    }

    public String getNotificationId(int pos) {
        return notifications.get(pos).id;
    }

    private class Notification {
        public final String id;
        public final Date time;
        public final String text;

        Notification(String id, long time, String text) {
            this.id = id;
            this.time = new Date(time * 1000);
            this.text = text;
        }
    }
}
