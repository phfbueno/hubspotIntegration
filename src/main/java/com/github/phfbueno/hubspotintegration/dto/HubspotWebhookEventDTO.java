package com.github.phfbueno.hubspotintegration.dto;

public record HubspotWebhookEventDTO(
        String subscriptionType,
        String objectType,
        String portalId,
        String appId,
        String objectId,
long eventId,
long occurredAt
) {}