package com.siberhus.zappos.cache.utils;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author Hussachai
 *
 * @param <T>
 */
public class CompletedFuture<T> implements Future<T> {
  
  private final T value;

  public CompletedFuture(final T value) {
    this.value = value;
  }
  
  @Override
  public boolean isDone() {
    return true;
  }

  @Override
  public T get() {
    return value;
  }
  
  @Override
  public T get(final long timeout, final TimeUnit unit) {
    return value;
  }
  
  @Override
  public boolean isCancelled() {
    return false;
  }
  
  @Override
  public boolean cancel(final boolean mayInterruptIfRunning) {
    return false;
  }
}