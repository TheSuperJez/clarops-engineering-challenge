package com.clara.challenge.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(schema = "clarops_challenge_schema", name = "traces")
public class TraceEntity {

  @Id
  @Column(name = "trace_id", nullable = false, length = 255)
  private String traceId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 50)
  @Builder.Default
  private TraceStatus status = TraceStatus.STARTED;

  @Column(name = "last_event_name", nullable = false, length = 255)
  private String lastEventName;

  @Column(name = "last_event_result", nullable = false, length = 50)
  private String lastEventResult;

  @Column(name = "next_expected_event", length = 255)
  private String nextExpectedEvent;

  @Column(name = "next_expected_before", columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private Instant nextExpectedBefore;

  @Column(name = "events_count", nullable = false)
  @Builder.Default
  private Integer eventsCount = 1;

  @CreationTimestamp
  @Column(
      name = "created_at",
      nullable = false,
      updatable = false,
      columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private Instant createdAt;

  @UpdateTimestamp
  @Column(
      name = "updated_at",
      nullable = false,
      columnDefinition = "TIMESTAMP WITH TIME ZONE")
  private Instant updatedAt;
}
