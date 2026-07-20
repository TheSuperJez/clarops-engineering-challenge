package com.clara.challenge.exception;

import java.io.Serial;

public class TraceNotFoundException extends RuntimeException {

  @Serial private static final long serialVersionUID = 1L;

  public TraceNotFoundException(String traceId) {
    super("Trace not found: " + traceId);
  }
}
