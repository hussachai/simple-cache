package com.siberhus.zappos.cache.exception;

import java.io.IOException;

/**
 * 
 * Analogous to IOException but it's runtime exception.
 * 
 * @author Hussachai
 *
 */
public class ReadWriteException extends CacheException{
  
  private static final long serialVersionUID = 1L;

  public ReadWriteException(String message) {
    super(message);
  }
  
  public ReadWriteException(IOException cause) {
    super(cause);
  }
  
}
