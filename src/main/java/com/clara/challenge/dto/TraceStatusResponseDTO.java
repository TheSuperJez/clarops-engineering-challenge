package com.clara.challenge.dto;

import com.clara.challenge.entity.TraceStatus;
import java.time.Instant;

public record TraceStatusResponseDTO(
    String traceId,
    TraceStatus status,
    String lastEventName,
    String lastEventResult,
    String nextExpectedEvent,
    Instant nextExpectedBefore,
    Integer eventsReceived) {}
