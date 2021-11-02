package org.georchestra.geonetwork.security.integration;

import org.geonetwork.security.external.integration.ScheduledAccountsSynchronizationService;
import org.georchestra.security.model.SecurityEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import groovy.util.logging.Slf4j;

@Service
@EnableWebMvc
@RestController
@RequestMapping(value = { SecurityEventsHttpSubscriber.SYNCHRONIZE_URI })
@Slf4j
public class SecurityEventsHttpSubscriber {

    public static final String SYNCHRONIZE_URI = "/srv/georchestra/internal/security/synchronize";

    private @Autowired ScheduledAccountsSynchronizationService synchronizationService;

    @PostMapping // (consumes = "application/json")
    public void acceptSecurityObjectEvent(@RequestBody(required = false) SecurityEvent<?> event) {
        System.err.println(event);
        synchronizationService.tryScheduleImmediately();
    }
}
