package org.modelmapper.internal;

/**
 * @author Jonathan Halterman
 */
class ErrorsException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  private final Errors errors;

  ErrorsException(Errors errors) {
    this.errors = errors;
  }

  public Errors getErrors() {
    return errors;
  }
}
