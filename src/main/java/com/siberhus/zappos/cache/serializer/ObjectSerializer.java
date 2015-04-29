package com.siberhus.zappos.cache.serializer;

import java.util.concurrent.Future;

/**
 * 
 * @author Hussachai
 *
 */
public interface ObjectSerializer {
  
  public CacheEntry read(String key);
  
  public void write(CacheEntry entry);
  
  public void tryWrite(CacheEntry entry);
  
  public Future<Boolean> tryDelete(String key);
  
}
