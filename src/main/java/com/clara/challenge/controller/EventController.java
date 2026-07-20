package com.clara.challenge.controller;

import com.clara.challenge.dto.EventRequestDTO;
import com.clara.challenge.service.WatchdogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

  private final WatchdogService watchdogService;

  @PostMapping
  public ResponseEntity<Void> receiveEvent(@Valid @RequestBody EventRequestDTO request) {
    watchdogService.processEvent(request);
    return ResponseEntity.ok().build();
  }
}
