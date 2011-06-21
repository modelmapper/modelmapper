package org.modelmapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.modelmapper.internal.Errors;
import org.modelmapper.spi.ErrorMessage;

/**
 * Indicates that an error has occurred during a validate operation.
 * 
 * @author Jonathan Halterman
 */
public class ValidationException extends RuntimeException {
  private static final long serialVersionUID = 0;
  private final List<ErrorMessage> messages;

  public ValidationException(List<ErrorMessage> messages) {
    this.messages = new ArrayList<ErrorMessage>(messages);
    initCause(Errors.getOnlyCause(this.messages));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getMessage() {
    return Errors.format("ModelMapper validation errors", messages);
  }

  /** Returns messages for the errors that caused this exception. */
  public Collection<ErrorMessage> getErrorMessages() {
    return messages;
  }
}
