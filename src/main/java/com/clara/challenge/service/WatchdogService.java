package com.clara.challenge.service;

import com.clara.challenge.dto.EventRequestDTO;
import com.clara.challenge.dto.TraceStatusResponseDTO;
import com.clara.challenge.entity.EventEntity;
import com.clara.challenge.entity.TraceEntity;
import com.clara.challenge.entity.TraceStatus;
import com.clara.challenge.exception.DuplicateEventException;
import com.clara.challenge.exception.TraceNotFoundException;
import com.clara.challenge.repository.EventRepository;
import com.clara.challenge.repository.TraceRepository;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class WatchdogService {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private final TraceRepository traceRepository;
  private final EventRepository eventRepository;

  @Transactional
  public void processEvent(EventRequestDTO dto) {
    log.info("Processing event: eventId={}, traceId={}, eventName={}", dto.eventId(), dto.traceId(),
        dto.eventName());

    if (eventRepository.existsById(dto.eventId())) {
      log.warn("Duplicate event detected: eventId={}", dto.eventId());
      throw new DuplicateEventException("Event already processed: " + dto.eventId());
    }

    TraceEntity trace =
        traceRepository
            .findById(dto.traceId())
            .map(existing -> updateExistingTrace(existing, dto))
            .orElseGet(() -> createNewTrace(dto));
    traceRepository.saveAndFlush(trace);

    Instant now = Instant.now();
    boolean finalEvent = dto.finalEvent() != null && dto.finalEvent();

    EventEntity event =
        EventEntity.builder()
            .eventId(dto.eventId())
            .traceId(dto.traceId())
            .eventName(dto.eventName())
            .result(dto.result())
            .occurredAt(dto.occurredAt())
            .receivedAt(now)
            .nextExpectedEvent(dto.nextExpectedEvent())
            .nextEventTtlSeconds(dto.nextEventTtlSeconds())
            .finalEvent(finalEvent)
            .metadata(serializeMetadata(dto.metadata()))
            .build();

    eventRepository.saveAndFlush(event);

    log.info("Event processed successfully: eventId={}, traceId={}, status={}", dto.eventId(),
        dto.traceId(), trace.getStatus());
  }

  private String serializeMetadata(java.util.Map<String, Object> metadata) {
    if (metadata == null || metadata.isEmpty()) {
      return null;
    }
    try {
      return objectMapper.writeValueAsString(metadata);
    } catch (JacksonException e) {
      log.warn("Failed to serialize metadata, storing as null", e);
      return null;
    }
  }

  private TraceEntity createNewTrace(EventRequestDTO dto) {
    Instant now = Instant.now();
    boolean finalEvent = dto.finalEvent() != null && dto.finalEvent();

    TraceEntity.TraceEntityBuilder builder =
        TraceEntity.builder()
            .traceId(dto.traceId())
            .lastEventName(dto.eventName())
            .lastEventResult(dto.result())
            .eventsCount(1);

    if (finalEvent) {
      builder.status(TraceStatus.COMPLETED);
    } else if (dto.nextExpectedEvent() != null && dto.nextEventTtlSeconds() != null) {
      builder
          .status(TraceStatus.WAITING_OTHER_EVENT)
          .nextExpectedEvent(dto.nextExpectedEvent())
          .nextExpectedBefore(now.plusSeconds(dto.nextEventTtlSeconds()));
    } else {
      builder.status(TraceStatus.STARTED);
    }

    return builder.build();
  }

  private TraceEntity updateExistingTrace(TraceEntity trace, EventRequestDTO dto) {
    trace.setEventsCount(trace.getEventsCount() + 1);
    trace.setLastEventName(dto.eventName());
    trace.setLastEventResult(dto.result());

    boolean finalEvent = dto.finalEvent() != null && dto.finalEvent();

    if (finalEvent) {
      trace.setStatus(TraceStatus.COMPLETED);
      trace.setNextExpectedEvent(null);
      trace.setNextExpectedBefore(null);
    } else if (dto.nextExpectedEvent() != null && dto.nextEventTtlSeconds() != null) {
      trace.setStatus(TraceStatus.WAITING_OTHER_EVENT);
      trace.setNextExpectedEvent(dto.nextExpectedEvent());
      trace.setNextExpectedBefore(Instant.now().plusSeconds(dto.nextEventTtlSeconds()));
    } else {
      trace.setStatus(TraceStatus.STARTED);
      trace.setNextExpectedEvent(null);
      trace.setNextExpectedBefore(null);
    }

    return trace;
  }

  @Transactional
  public TraceStatusResponseDTO getTraceStatus(String traceId) {
    log.info("Retrieving trace status: traceId={}", traceId);

    TraceEntity trace =
        traceRepository
            .findById(traceId)
            .orElseThrow(() -> new TraceNotFoundException(traceId));

    if (trace.getStatus() == TraceStatus.WAITING_OTHER_EVENT
        && trace.getNextExpectedBefore() != null
        && Instant.now().isAfter(trace.getNextExpectedBefore())) {
      log.info("TTL expired for trace: traceId={}", traceId);
      trace.setStatus(TraceStatus.TTL_EXPIRED_FOR_EVENT);
      traceRepository.saveAndFlush(trace);
    }

    return new TraceStatusResponseDTO(
        trace.getTraceId(),
        trace.getStatus(),
        trace.getLastEventName(),
        trace.getLastEventResult(),
        trace.getNextExpectedEvent(),
        trace.getNextExpectedBefore(),
        trace.getEventsCount());
  }
}
