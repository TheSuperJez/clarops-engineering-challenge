package com.clara.challenge.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;
import org.springframework.data.domain.Persistable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "clarops_challenge_schema", name = "events")
public class EventEntity implements Persistable<String> {

  @Id
  @Column(name = "event_id", nullable = false, length = 255)
  private String eventId;

  @Column(name = "trace_id", nullable = false, length = 255)
  private String traceId;

  @Column(name = "event_name", nullable = false, length = 255)
  private String eventName;

  @Column(nullable = false, length = 50)
  private String result;

  @Column(name = "occurred_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private Instant occurredAt;

  @Column(name = "received_at", nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private Instant receivedAt;

  @Column(name = "next_expected_event", length = 255)
  private String nextExpectedEvent;

  @Column(name = "next_event_ttl_seconds")
  private Integer nextEventTtlSeconds;

  @Column(name = "final_event", nullable = false)
  @Builder.Default
  private Boolean finalEvent = false;

  @ColumnTransformer(write = "?::jsonb")
  @Column(name = "metadata", columnDefinition = "JSONB")
  private String metadata;

  @Override
  @Transient
  public String getId() {
    return eventId;
  }

  @Override
  @Transient
  public boolean isNew() {
    return true;
  }
}
