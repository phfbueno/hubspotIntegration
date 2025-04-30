package com.github.phfbueno.hubspotintegration.controller;

import com.github.phfbueno.hubspotintegration.dto.HubspotWebhookEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);


    @PostMapping("/hubspot/contact")
    public ResponseEntity<Void> receiveContactCreationWebhook(@RequestBody List<HubspotWebhookEventDTO> events) {
        log.info("Webhook recebido do HubSpot com {} evento(s)", events.size());

        for (HubspotWebhookEventDTO event : events) {
            if ("contact".equals(event.objectType()) && "creation".equals(event.subscriptionType())) {
                log.info("Contato criado com ID: {}", event.objectId());
            }
        }

        return ResponseEntity.ok().build();
    }
}
