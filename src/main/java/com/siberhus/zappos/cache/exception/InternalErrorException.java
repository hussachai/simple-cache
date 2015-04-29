package com.siberhus.zappos.cache.exception;

/**
 * Analogous to any exceptions relating to class loading and reflection.
 * @author Hussachai
 *
 */
public class InternalErrorException extends CacheException {
  
  private static final long serialVersionUID = 1L;

  public InternalErrorException(Throwable cause) {
    super(cause);
  }
  
}
