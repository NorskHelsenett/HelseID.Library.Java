package no.helseid.exceptions;

/**
 * Generic Exception wrapping exceptions occuring in this library
 */
public class HelseIdException extends Exception {
  /**
   * Create an exception with no external cause
   * @param message message describing the exception
   */
  public HelseIdException(String message) {
    super(message);
  }

  /**
   * Create an exception with an external cause
   * @param message message describing the exception
   * @param cause the cause of this exception
   */
  public HelseIdException(String message, Throwable cause) {
    super(message, cause);
  }
}
