package com.siberhus.zappos.cache.utils;

/**
 * 
 * 
 * @author Hussachai
 *
 */
public final class ConfigKeys {
  
  public static final class Cache {
    
    /**
     * Path to file cache. Expect directory.
     */
    public static final String DISK_STORE = "cache.diskStore";
  
    /**
     * Max number of items in memory
     */
    public static final String MAX_ITEM = "cache.maxItem";
  
    /**
     * Object serializer implementation. Expect FQ class name.
     */
    public static final String SERIALIZER = "cache.serializer";
  }
  
  public static final class Thread {
    
    /**
     * Number of threads for I/O operations
     */
    public static final String MAX = "thread.max";
    
    /**
     * Wait time for shutting down the thread pool in second
     */
    public static final String AWAIT_SHUTDOWN_TIME = "thread.awaitShutdownTime";
    
  }
  
  
}
