package com.clara.challenge.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import java.util.Map;

public record EventRequestDTO(
    @NotBlank @Size(max = 255) String eventId,
    @NotBlank @Size(max = 255) String traceId,
    @NotBlank @Size(max = 255) String eventName,
    @NotBlank @Size(max = 50) String result,
    @NotNull Instant occurredAt,
    @Size(max = 255) String nextExpectedEvent,
    @Positive Integer nextEventTtlSeconds,
    Boolean finalEvent,
    Map<String, Object> metadata) {}
