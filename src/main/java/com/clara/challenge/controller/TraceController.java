package com.clara.challenge.controller;

import com.clara.challenge.dto.TraceStatusResponseDTO;
import com.clara.challenge.service.WatchdogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/traces")
@RequiredArgsConstructor
public class TraceController {

  private final WatchdogService watchdogService;

  @GetMapping("/{traceId}/status")
  public ResponseEntity<TraceStatusResponseDTO> getTraceStatus(@PathVariable String traceId) {
    TraceStatusResponseDTO response = watchdogService.getTraceStatus(traceId);
    return ResponseEntity.ok(response);
  }
}
