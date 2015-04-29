package com.siberhus.zappos.cache;

import java.util.concurrent.Future;

/**
 * 
 * Simple cache interface
 * 
 * @author Hussachai
 *
 */
public interface Cache {
  
  /**
   * Get element from cache.
   * 
   * @param key
   * @return
   */
  public Element get(String key);
  
  /**
   * 
   * @param key
   * @param element
   */
  public void set(String key, Element element);
  
  /**
   * Remove value from cache by specified key.
   * If the value is in memory, this will remove immediately.
   * Otherwise, it may take a bit of time to finish.
   * If you want to make sure that the delete operation is already done,
   * you have to call get method on a returning Future object.
   * @param key
   * @return
   */
  public Future<Boolean> evict(String key);
  
  /**
   * Caution: This method return number of element in memory.
   * It doesn't reflect the total number of elements that cache holds
   * @return number of element in memory
   */
  public int size();
  
}
