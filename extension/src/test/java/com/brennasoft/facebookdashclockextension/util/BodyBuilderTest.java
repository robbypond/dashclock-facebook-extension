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
import com.brennasoft.facebookdashclockextension.util.BodyBuilder;

import org.junit.runner.RunWith;
import org.junit.Test;

import com.brennasoft.facebookdashclockextension.util.TitleBuilder;

import java.util.Date;

import static org.junit.Assert.*;

@RunWith(RobolectricGradleTestRunner.class)
public class BodyBuilderTest {

    @Test
    public void should_build_blank() {
        String body = "";
        BodyBuilder bodyBuilder = new BodyBuilder(createInvalidNotificationResponse(), createInvalidInboxResponse());
        assertEquals(body, bodyBuilder.build());
    }

    @Test
    public void should_build_katie_commented() {
        String body = "Katie commented on Steven Lemire's post";

        NotificationsResponse notificationsResponse = createNotificationResponseWithNotification(1397703662);
        InboxResponse inboxResponse = createInboxResponse(0);
        BodyBuilder builder = new BodyBuilder(notificationsResponse, inboxResponse);
        assertEquals(body, builder.build());

        builder = new BodyBuilder(notificationsResponse, createInvalidInboxResponse());
        assertEquals(body, builder.build());

        builder = new BodyBuilder(notificationsResponse, null);
        assertEquals(body, builder.build());

        inboxResponse = createInboxWithMessage(1397703661);
        builder = new BodyBuilder(notificationsResponse, inboxResponse);
        assertEquals(body, builder.build());
    }

    @Test
    public void should_build_chris_message() {
        String body = "Chris Akins: Have you been flying solo for like 4 days?  What in the world have you done with yourself?  That sounds pretty sweet.";

        NotificationsResponse notificationsResponse = createNotificationResponseWithNotification(1397703661);
        InboxResponse inboxResponse = createInboxWithMessage(1397703662);
        BodyBuilder builder = new BodyBuilder(notificationsResponse, inboxResponse);
        assertEquals(body, builder.build());

        builder = new BodyBuilder(createInvalidNotificationResponse(), inboxResponse);
        assertEquals(body, builder.build());

        builder = new BodyBuilder(null, inboxResponse);
        assertEquals(body, builder.build());
    }

    private NotificationsResponse createNotificationResponseWithNotification(long time) {
        NotificationsResponse notificationsResponse = createNotificationResponse(1);
        notificationsResponse.addNotification("1", time, "Katie commented on Steven Lemire's post");
        return notificationsResponse;
    }

    private InboxResponse createInboxWithMessage(long time) {
        InboxResponse inboxResponse = createInboxResponse(1);
        inboxResponse.author = "Chris Akins";
        inboxResponse.snippet = "Have you been flying solo for like 4 days?  What in the world have you done with yourself?  That sounds pretty sweet.";
        inboxResponse.time = new Date(time*1000);
        return inboxResponse;
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
