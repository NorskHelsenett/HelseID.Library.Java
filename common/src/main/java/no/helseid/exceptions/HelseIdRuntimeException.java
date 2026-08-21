package no.helseid.exceptions;

/**
 * Generic RuntimeExceptions wrapping exceptions occuring runtime in this library
 */
public class HelseIdRuntimeException extends RuntimeException {
  /**
   * Create an exception with no external cause
   * @param message message describing the exception
   */
  public HelseIdRuntimeException(String message) {
    super(message);
  }

  /**
   * Create an exception with an external cause
   * @param message message describing the exception
   * @param cause the cause of this exception
   */
  public HelseIdRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
}
