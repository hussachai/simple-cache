package com.siberhus.zappos.cache.serializer;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.siberhus.zappos.cache.utils.CompletedFuture;
import com.siberhus.zappos.cache.utils.PrintlnLogger;

/**
 * 
 * @author Hussachai
 * 
 */
public abstract class FileBasedObjectSerializer implements ObjectSerializer {
  
  protected PrintlnLogger log = PrintlnLogger.INSTANCE;
  
  /**
   * The delete queue storing as Map. It's queued by thread pool.
   * Map of key and created date
   */
  private Map<String, Date> pendingDeleteFiles = new ConcurrentHashMap<>(); 
  
  /**
   * This map is the same as LRUCache but it stores only the key that is being
   * flushed from memory. After the cache entry has been saved, it will be removed
   * from this cache.
   */
  private Map<String, CacheEntry> pendingWriteCache = new ConcurrentHashMap<>();
  
  private ExecutorService executorService;
  
  private File diskStore;
  
  public abstract CacheEntry readFromFile(File file);
  
  public abstract void writeToFile(File file, CacheEntry entry);
  
  public void setDiskStore(File file) {
    if (file == null) {
      throw new IllegalArgumentException("file cannot be null");
    }
    if (!file.exists() && !file.mkdirs()) {
      throw new IllegalArgumentException(
          "file does not exist and cannot be created");
    } else if (!file.isDirectory()) {
      throw new IllegalArgumentException("file must be directory");
    }
    this.diskStore = file;
  }
  
  public void setExecutorService(ExecutorService executorService){
    this.executorService = executorService;
  }
  
  @Override
  public CacheEntry read(String key) {
    if(pendingDeleteFiles.containsKey(key)){
      /* This is in delete pending queue. The data may be expire.
       * Another possibility is that the data is deleting
       * and we may get exception when read it from file.
       * */
      return null;
    }
    CacheEntry entry = pendingWriteCache.get(key);
    if(entry != null){
      return entry;
    }
    File file = getCacheFile(key);
    return readFromFile(file);
  }
  
  @Override
  public void write(CacheEntry entry)  {
    File file = getCacheFile(entry.getKey());
    File dir = file.getParentFile();
    if(dir != null && !dir.exists()){
      dir.mkdirs();
    }
    writeToFile(file, entry);
  }
  
  @Override
  public void tryWrite(CacheEntry entry){
    log.debug("Submitting file cache writer task for entry: {0}", entry);
    executorService.execute(new FileWriteTask(entry));
  }
  
  @Override
  public Future<Boolean> tryDelete(String key){
    File file = getCacheFile(key);
    if(file.exists()){
      log.debug("Submitting file cache delete task for key: {0}", key);
      return executorService.submit(new FileDeleteTask(key, file));
    }
    log.debug("Trying to delete file cache for key: {0} but it doesn't exist", key);
    return new CompletedFuture<Boolean>(false);
  }
  
  protected File getCacheFile(String key) {
    String hash = hashKey(key);
    /* Crate a lot of files in the same dir can slow down file explorer.
     */
    String dirName = diskStore + File.separator + hash.substring(0, 8);
    return new File(dirName + File.separator + hash.substring(8));
  }
  
  /**
   * MD5 is not the best algorithm to pick and 
   * the implementation that ship with JDK is not the fastest.
   * 
   * @param input
   * @return
   */
  private String hashKey(String input) {
    try {
      MessageDigest md5 = MessageDigest.getInstance("MD5");
      md5.update(input.getBytes());
      StringBuilder output = new StringBuilder();
      //we add two more characters to represent the length
      //and the first character of the input respectively. 
      //just to reduce the key collision when the data 
      //are different but the hash are the same.
      output.append(String.valueOf(input.length()));
      output.append(String.valueOf((int)input.charAt(0)));
      for (byte b : md5.digest()) {
        output.append(String.format("%02x", b & 0xff));
      }
      return output.toString();
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
 
  class FileWriteTask implements Runnable{
    
    private CacheEntry entry;
    
    public FileWriteTask(CacheEntry entry){
      this.entry = entry;
      pendingWriteCache.put(entry.getKey(), entry);
    }
    
    @Override
    public void run(){
      
      log.debug("Writing cache entry: {0} to file", entry);
      
      write(entry);
      
      pendingWriteCache.remove(entry.getKey());
    }
    
  }
  class FileDeleteTask implements Callable<Boolean>{

    private String key;
    private File file;
    
    public FileDeleteTask(String key, File file){
      this.key = key;
      this.file = file;
      pendingDeleteFiles.put(key, new Date());
    }
    
    @Override
    public Boolean call() {
      log.debug("Deleting file for key: {0}", key);
      if(!pendingDeleteFiles.containsKey(key)){
        return false;
      }
      
      if(!file.delete()){
        /* Cannot delete it now. Let VM delete it later */
        log.warn("Cannot delete file: {0}. Asked JVM to do it later.", file);
        file.deleteOnExit();
        //TODO: should have scheduler for file sweeping
        return false;
      }
      
      pendingDeleteFiles.remove(key);
      
      log.debug("File cache for key: {0} has been deleted", key);
      
      return true;
    }
    
  }
  
  
}
