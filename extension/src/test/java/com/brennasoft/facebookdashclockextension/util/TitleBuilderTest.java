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
import com.brennasoft.facebookdashclockextension.util.TitleBuilder;

import android.app.Activity;
import android.content.res.Resources;

import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.Test;

import com.brennasoft.facebookdashclockextension.R;
import com.brennasoft.facebookdashclockextension.util.TitleBuilder;

import static org.junit.Assert.*;

@RunWith(RobolectricGradleTestRunner.class)
public class TitleBuilderTest {

    Resources resources;

    @Before
    public void setUp() throws Exception {
        Activity activity = new Activity();
        resources = activity.getResources();
    }

    @Test
    public void should_build_blank() {
        String title = "";
        TitleBuilder titleBuilder = new TitleBuilder(createInvalidNotificationResponse(), createInvalidInboxResponse(), resources);
        assertEquals(title, titleBuilder.buildCondensed());

        titleBuilder = new TitleBuilder(null, createInvalidInboxResponse(), resources);
        assertEquals(title, titleBuilder.buildCondensed());

        titleBuilder = new TitleBuilder(createInvalidNotificationResponse(), null, resources);
        assertEquals(title, titleBuilder.buildCondensed());

        titleBuilder = new TitleBuilder(null, null, resources);
        assertEquals(title, titleBuilder.buildCondensed());

        titleBuilder = new TitleBuilder(createInvalidNotificationResponse(), createInvalidInboxResponse(), resources);
        assertEquals(title, titleBuilder.build());

        titleBuilder = new TitleBuilder(null, createInvalidInboxResponse(), resources);
        assertEquals(title, titleBuilder.build());

        titleBuilder = new TitleBuilder(createInvalidNotificationResponse(), null, resources);
        assertEquals(title, titleBuilder.build());

        titleBuilder = new TitleBuilder(null, null, resources);
        assertEquals(title, titleBuilder.build());
    }

    @Test
    public void should_build_mail_0_updates_0() {
        String title = "Mail: 0 Updates: 0";
        InboxResponse inboxResponse = createInboxResponse(0);
        NotificationsResponse notificationsResponse = createNotificationResponse(0);
        TitleBuilder titleBuilder = new TitleBuilder(notificationsResponse, inboxResponse, resources);
        assertEquals(title, titleBuilder.buildCondensed());
    }

    @Test
    public void should_build_0_messages_0_notifications() {
        String title = "0 Messages / 0 Notifications";
        InboxResponse inboxResponse = createInboxResponse(0);
        NotificationsResponse notificationsResponse = createNotificationResponse(0);
        TitleBuilder titleBuilder = new TitleBuilder(notificationsResponse, inboxResponse, resources);
        assertEquals(title, titleBuilder.build());
    }

    @Test
    public void should_build_2_messages_16_notificatons() {
        int messageCount = 2;
        int notificationCount = 16;
        String title = resources.getQuantityString(R.plurals.message, messageCount, messageCount);
        title += " / ";
        title += resources.getQuantityString(R.plurals.notification, notificationCount, notificationCount);

        TitleBuilder titleBuilder = new TitleBuilder(createNotificationResponse(16), createInboxResponse(2), resources);
        assertEquals(title, titleBuilder.build());
    }

    @Test
    public void should_build_2_messages() {
        int messageCount = 2;
        String title = resources.getQuantityString(R.plurals.message, messageCount, messageCount);

        TitleBuilder titleBuilder = new TitleBuilder(null, createInboxResponse(2), resources);
        assertEquals(title, titleBuilder.build());

        titleBuilder = new TitleBuilder(createInvalidNotificationResponse(), createInboxResponse(2), resources);
        assertEquals(title, titleBuilder.build());
    }

    @Test
    public void should_build_16_notifications() {
        int notificationCount = 16;
        String title = resources.getQuantityString(R.plurals.notification, notificationCount, notificationCount);

        TitleBuilder titleBuilder = new TitleBuilder(createNotificationResponse(16), null, resources);
        assertEquals(title, titleBuilder.build());

        titleBuilder = new TitleBuilder(createNotificationResponse(16), createInvalidInboxResponse(), resources);
        assertEquals(title, titleBuilder.build());
    }

    @Test
    public void should_build_mail_2_updates_16() {
        int messageCount = 2;
        int notificationCount = 16;
        String title = resources.getString(R.string.message_condensed, messageCount);
        title += " ";
        title += resources.getString(R.string.updates_condensed, notificationCount);

        TitleBuilder titleBuilder = new TitleBuilder(createNotificationResponse(16), createInboxResponse(2), resources);
        assertEquals(title, titleBuilder.buildCondensed());
    }

    @Test
    public void should_build_mail_2() {
        int messageCount = 2;
        String title = resources.getString(R.string.message_condensed, messageCount);

        TitleBuilder titleBuilder = new TitleBuilder(null, createInboxResponse(2), resources);
        assertEquals(title, titleBuilder.buildCondensed());

        titleBuilder = new TitleBuilder(createInvalidNotificationResponse(), createInboxResponse(2), resources);
        assertEquals(title, titleBuilder.buildCondensed());
    }

    @Test
    public void should_build_updates_16() {
        int notificationCount = 16;
        String title = resources.getString(R.string.updates_condensed, notificationCount);

        TitleBuilder titleBuilder = new TitleBuilder(createNotificationResponse(16), null, resources);
        assertEquals(title, titleBuilder.buildCondensed());

        titleBuilder = new TitleBuilder(createNotificationResponse(16), createInvalidInboxResponse(), resources);
        assertEquals(title, titleBuilder.buildCondensed());
    }

    InboxResponse createInboxResponse(int count) {
        InboxResponse inboxResponse = new InboxResponse();
        inboxResponse.success = true;
        inboxResponse.count = count;
        return inboxResponse;
    }

    NotificationsResponse createNotificationResponse(int count) {
        NotificationsResponse notificationsResponse = new NotificationsResponse();
        notificationsResponse.success = true;
        notificationsResponse.count = count;
        return notificationsResponse;
    }

    InboxResponse createInvalidInboxResponse() {
        InboxResponse inboxResponse = new InboxResponse();
        inboxResponse.success = false;
        return inboxResponse;
    }

    NotificationsResponse createInvalidNotificationResponse() {
        NotificationsResponse notificationsResponse = new NotificationsResponse();
        notificationsResponse.success = false;
        return notificationsResponse;
    }
}
