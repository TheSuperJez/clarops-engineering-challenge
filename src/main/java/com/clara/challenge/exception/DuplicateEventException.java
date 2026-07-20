package com.clara.challenge.exception;

import java.io.Serial;

public class DuplicateEventException extends RuntimeException {

  @Serial private static final long serialVersionUID = 1L;

  public DuplicateEventException(String message) {
    super(message);
  }
}
