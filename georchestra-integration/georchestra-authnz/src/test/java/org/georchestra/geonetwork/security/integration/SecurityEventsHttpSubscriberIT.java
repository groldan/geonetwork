/*
 * Copyright (C) 2021 by the geOrchestra PSC
 *
 * This file is part of geOrchestra.
 *
 * geOrchestra is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * geOrchestra is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * geOrchestra.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.georchestra.geonetwork.security.integration;

import static org.georchestra.geonetwork.security.integration.SecurityEventsHttpSubscriber.SYNCHRONIZE_URI;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.Collections;
import java.util.List;

import org.geonetwork.security.external.integration.AccountsReconcilingService;
import org.georchestra.geonetwork.security.AbstractGeorchestraIntegrationTest;
import org.georchestra.security.api.UsersApi;
import org.georchestra.security.model.GeorchestraUser;
import org.georchestra.security.model.SecurityEvent;
import org.georchestra.security.model.SecurityEvent.EventType;
import org.georchestra.security.model.UserEvent;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.NonNull;

@WebAppConfiguration
public class SecurityEventsHttpSubscriberIT extends AbstractGeorchestraIntegrationTest {

    private @Autowired WebApplicationContext context;
    private MockMvc mockMvc;

    private @Autowired SecurityEventsHttpSubscriber removeEventsReceiver;
    private @SpyBean AccountsReconcilingService synchronizationService;

    private @Autowired UsersApi consoleUsersApiClient;

    @Before
    public void setupMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    private List<HandlerExceptionResolver> exceptionResolver() {
        return Collections.singletonList(new ExceptionHandlerExceptionResolver());
    }

    public @Test void testSynchronizesOnDirectCallWithNoEventPayload() throws Exception {
        removeEventsReceiver.acceptSecurityObjectEvent(null);
        verify(synchronizationService, times(1)).synchronize();
    }

    public @Test void testSynchronizesOnHttpCallWithNoEventPayload() throws Exception {
        mockMvc.perform(post(SYNCHRONIZE_URI));
        mockMvc.perform(post(SYNCHRONIZE_URI));
        verify(synchronizationService, times(1)).synchronize();
    }

    public @Test void testSynchronizesOnUserEvent() throws Exception {
        GeorchestraUser user = mock(GeorchestraUser.class);
        SecurityEvent<?> event = new UserEvent(EventType.Created, user);
        String body = new ObjectMapper().writeValueAsString(event);
        mockMvc.perform(post(SYNCHRONIZE_URI).content(body));
    }
}
