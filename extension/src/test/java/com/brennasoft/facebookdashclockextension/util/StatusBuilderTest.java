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
import com.brennasoft.facebookdashclockextension.util.StatusBuilder;

import org.junit.runner.RunWith;
import org.junit.Test;

import dalvik.annotation.TestTarget;

import static org.junit.Assert.*;

@RunWith(RobolectricGradleTestRunner.class)
public class StatusBuilderTest {

    @Test
    public void should_build_blank() {
        String status = "";
        StatusBuilder statusBuilder = new StatusBuilder(createInvalidNotificationResponse(), createInvalidInboxResponse());
        assertEquals(status, statusBuilder.buildCondensed());

        statusBuilder = new StatusBuilder(null, createInvalidInboxResponse());
        assertEquals(status, statusBuilder.buildCondensed());

        statusBuilder = new StatusBuilder(createInvalidNotificationResponse(), null);
        assertEquals(status, statusBuilder.buildCondensed());

        statusBuilder = new StatusBuilder(null, null);
        assertEquals(status, statusBuilder.buildCondensed());
    }

    @Test
    public void should_build_0() {
        String status = "0";
        InboxResponse inboxResponse = createInboxResponse(0);
        NotificationsResponse notificationsResponse = createNotificationResponse(0);
        StatusBuilder statusBuilder = new StatusBuilder(notificationsResponse, inboxResponse);
        assertEquals(status, statusBuilder.buildCondensed());

        statusBuilder = new StatusBuilder(null, inboxResponse);
        assertEquals(status, statusBuilder.buildCondensed());

        statusBuilder = new StatusBuilder(notificationsResponse, null);
        assertEquals(status, statusBuilder.buildCondensed());

        statusBuilder = new StatusBuilder(notificationsResponse, createInvalidInboxResponse());
        assertEquals(status, statusBuilder.buildCondensed());

        statusBuilder = new StatusBuilder(createInvalidNotificationResponse(), inboxResponse);
        assertEquals(status, statusBuilder.buildCondensed());

        statusBuilder = new StatusBuilder(notificationsResponse, null);
        assertEquals(status, statusBuilder.build());

        statusBuilder = new StatusBuilder(null, inboxResponse);
        assertEquals(status, statusBuilder.build());

        statusBuilder = new StatusBuilder(notificationsResponse, createInvalidInboxResponse());
        assertEquals(status, statusBuilder.build());

        statusBuilder = new StatusBuilder(createInvalidNotificationResponse(), inboxResponse);
        assertEquals(status, statusBuilder.build());
    }

    @Test
    public void should_build_5() {
        String status = "5";
        StatusBuilder statusBuilder = new StatusBuilder(createNotificationResponse(0), createInboxResponse(5));
        assertEquals(statusBuilder.buildCondensed(), status);

        statusBuilder = new StatusBuilder(createNotificationResponse(5), createInboxResponse(0));
        assertEquals(status, statusBuilder.buildCondensed());

        statusBuilder = new StatusBuilder(createNotificationResponse(2), createInboxResponse(3));
        assertEquals(status, statusBuilder.buildCondensed());

        statusBuilder = new StatusBuilder(createNotificationResponse(5), null);
        assertEquals(status, statusBuilder.buildCondensed());

        statusBuilder = new StatusBuilder(null, createInboxResponse(5));
        assertEquals(status, statusBuilder.buildCondensed());

        statusBuilder = new StatusBuilder(createNotificationResponse(5), createInvalidInboxResponse());
        assertEquals(status, statusBuilder.buildCondensed());

        statusBuilder = new StatusBuilder(createInvalidNotificationResponse(), createInboxResponse(5));
        assertEquals(status, statusBuilder.buildCondensed());
    }

    @Test
    public void should_build_0_slash_0() {
        String status = "0 / 0";
        InboxResponse inboxResponse = createInboxResponse(0);
        NotificationsResponse notificationsResponse = createNotificationResponse(0);
        StatusBuilder statusBuilder = new StatusBuilder(notificationsResponse, inboxResponse);
        assertEquals(status, statusBuilder.build());
    }

    @Test
    public void should_build_2_slash_1() {
        String status = "2 / 1";
        InboxResponse inboxResponse = createInboxResponse(2);
        NotificationsResponse notificationsResponse = createNotificationResponse(1);
        StatusBuilder statusBuilder = new StatusBuilder(notificationsResponse, inboxResponse);
        assertEquals(status, statusBuilder.build());
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
