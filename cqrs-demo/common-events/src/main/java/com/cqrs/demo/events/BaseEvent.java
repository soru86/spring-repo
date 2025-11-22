package com.cqrs.demo.events;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class BaseEvent {
    private UUID eventId;
    private LocalDateTime timestamp;
    private String eventType;

    public BaseEvent() {
        this.eventId = UUID.randomUUID();
        this.timestamp = LocalDateTime.now();
        this.eventType = this.getClass().getSimpleName();
    }

    public BaseEvent(UUID eventId, LocalDateTime timestamp) {
        this.eventId = eventId;
        this.timestamp = timestamp;
        this.eventType = this.getClass().getSimpleName();
    }

    public UUID getEventId() {
        return eventId;
    }

    public void setEventId(UUID eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}

