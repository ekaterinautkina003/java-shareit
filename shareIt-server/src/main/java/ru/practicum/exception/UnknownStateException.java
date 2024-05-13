package ru.practicum.exception;

public class UnknownStateException extends RuntimeException {

  public UnknownStateException(String message) {
    super("Unknown state: " + message);
  }
}
