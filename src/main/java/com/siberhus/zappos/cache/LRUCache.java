package com.siberhus.zappos.cache;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.siberhus.zappos.cache.exception.InternalErrorException;
import com.siberhus.zappos.cache.exception.ReadWriteException;
import com.siberhus.zappos.cache.serializer.CacheEntry;
import com.siberhus.zappos.cache.serializer.FileBasedObjectSerializer;
import com.siberhus.zappos.cache.serializer.ObjectSerializer;
import com.siberhus.zappos.cache.serializer.StandardSerializer;
import com.siberhus.zappos.cache.utils.Config;
import com.siberhus.zappos.cache.utils.ConfigKeys;
import com.siberhus.zappos.cache.utils.FileUtils;
import com.siberhus.zappos.cache.utils.PrintlnLogger;

/**
 * Cache that maintains the order of access and discard the oldest element
 * when the specified limit has reached. It also supports secondary level cache
 * using file system. Most I/O operations are handled in asynchronous fashion
 * to ensure that they will not slow you down. Fine grained TTL is supported
 * to mitigate stale data issue when you have no control over it and you cannot
 * evict the cache on data modification event. 
 *  
 * Thread-safe
 * 
 * @author Hussachai
 *
 */
public class LRUCache implements Cache {
  
  /**
   * LRU Map is implemented based on LinkedHashMap
   * Not thread-safe.
   * 
   * @author Hussachai
   *
   */
  class LRUMap extends LinkedHashMap<String, Element>{
    
    private static final long serialVersionUID = 1L;
    
    private ObjectSerializer serializer;
    
    /**
     * Max element allowed in Map
     */
    private int maxSize;
    
    public LRUMap(ObjectSerializer serializer, int maxSize) {
      super(maxSize + 1, 1.0f, true);/* make sure accessOrder is true */
      this.serializer = serializer;
      this.maxSize = maxSize;
    }
    
    /**
     * Get element from map and re-position the order of elements
     */
    @Override
    public Element get(Object key) {
      return super.get(key);
    }
    
    @Override
    public Element put(String key, Element value) {
      return super.put(key, value);
    }
    
    @Override
    protected boolean removeEldestEntry(Entry<String, Element> eldest) {
      boolean remove = super.size() > maxSize;
      if(remove){
        /* Create new thread for writing to CacheEntry to file */
        CacheEntry entry = new CacheEntry(eldest.getKey(), eldest.getValue());
        /* Make sure that we're not saving expired element */
        if(entry.getElement().getTTL() != 0){
          log.debug("Saving key: {0} to file", entry);
          serializer.tryWrite(entry);
        }
      }
      return remove;
    }
    
  };
  
  /**
   * An object serializer for a cache entry that is taken out of memory
   */
  private ObjectSerializer serializer;
  
  private Map<String, Element> lruMap;
  
  /**
   * A thread-pool manager for managing either long running or blocking I/O task.
   */
  private ExecutorService executorService;
  
  private PrintlnLogger log = PrintlnLogger.INSTANCE;
  
  public LRUCache(String name){
    this(name, new Config());
  }
  
  public LRUCache(String name, final Config config){
    
    try{
      if(config.size() == 0){
        log.info("Using default configuration");
        config.load(getClass().getResourceAsStream("/cache.properties"));
      }
      log.debug("Configurations");
      for(Entry<Object, Object> entry: config.entrySet()){
        log.debug("{0} = {1}", entry.getKey(), entry.getValue());
      }
      
      executorService = Executors.newFixedThreadPool(
          config.getInt(ConfigKeys.Thread.MAX, 3));
      
      Runtime.getRuntime().addShutdownHook(new Thread(){
        public void run(){
          try {
            int waitTime = config.getInt(ConfigKeys.Thread.AWAIT_SHUTDOWN_TIME, 10);
            log.info("Shutting down thread pool... wait for {0} seconds", waitTime);
            executorService.awaitTermination(waitTime, TimeUnit.SECONDS);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      });
      
      serializer = (ObjectSerializer)Class.forName(
          config.getString(ConfigKeys.Cache.SERIALIZER, 
          StandardSerializer.class.getName())).newInstance();
      
      String cacheDirPath = FileUtils.ensureEndingSeparater(config.getString(
          ConfigKeys.Cache.DISK_STORE, System.getProperty("java.io.tmpdir") 
            + "simple-cache")) + name;
      
      /* Delete all old cache files
       * This is intended to be blocking I/O
       */
      File cacheDir = new File(cacheDirPath);
      FileUtils.deleteFiles(cacheDir);
      
      if(serializer instanceof FileBasedObjectSerializer){
        FileBasedObjectSerializer aos = ((FileBasedObjectSerializer)serializer);
        aos.setDiskStore(cacheDir);
        aos.setExecutorService(executorService);
      }
      
      lruMap = new LRUMap(serializer, config.getInt(
          ConfigKeys.Cache.MAX_ITEM, 10000));
      
    }catch(IOException e){
      throw new ReadWriteException(e);
    }catch(ReflectiveOperationException e){
      throw new InternalErrorException(e);
    }
  }
  
  @Override
  public synchronized Element get(String key) {
    if(key == null){
      throw new IllegalArgumentException("key cannot be null");
    }
    Element value = lruMap.get(key);
    if(value == null){
      log.debug("Key: {0} not found in memory. Trying to look up in disk", key);
      /* Possibly in disk */
      CacheEntry entry = serializer.read(key);
      if(entry != null){
        value = entry.getElement();
        lruMap.put(key, value);
        /* Create a new thread for I/O operation */
        serializer.tryDelete(key);
      }else{
        log.debug("Key: {0} not found in disk.", key);
      }
    }
    if(value != null){
      long ttl = value.getTTL();
      if(ttl != -1){
        if(ttl == 0){
          evict(key);/* probably stale data */ 
          return null;
        }else{
          value.touch();
        }
      }
    }
    return value;
  }
  
  @Override
  public synchronized void set(String key, Element element) {
    if(key == null){
      throw new IllegalArgumentException("key cannot be null");
    }
    lruMap.put(key, element);
  }
  
  @Override
  public synchronized int size(){
    return lruMap.size();
  }
  
  @Override
  public synchronized Future<Boolean> evict(String key) {
    lruMap.remove(key);
    return serializer.tryDelete(key);
  }
  
}
