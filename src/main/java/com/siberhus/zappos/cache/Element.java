package com.siberhus.zappos.cache;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 
 * Basic object wrapper for cache value.
 *  
 * @author Hussachai
 *
 */
public class Element implements Serializable{
  
  private static final long serialVersionUID = -7709589260379163911L;

  private Serializable value;
  
  /** TTL in second **/
  private long ttl;
  
  private Date lastAccess = new Date();
  
  public Element(Serializable value){
    this(value, -1);
  }
  
  public Element(Serializable value, long ttl){
    this.value = value;
    this.ttl = ttl;
  }
  
  @Override
  public String toString(){
    return value.toString();
  }
  
  
  /**
   * A bit better to way to set TTL than via constructure.
   * @param n
   * @param unit
   * @return
   */
  public Element ttl(int n, TimeUnit unit){
    
    this.ttl = unit.toSeconds(n);
    return this;
  }
  
  /**
   * Update timestamp. Make data fresh again.
   * @return
   */
  public Element touch(){
    lastAccess = new Date();
    return this;
  }

  /**
   * 
   * @return
   */
  public Serializable getValue() {
    return value;
  }
  
  /**
   * The freshness of data.
   * @return -1 never expires, 0 already expired (don't eat), otherwise it's still fresh.
   */
  public long getTTL() {
    
    if(ttl == -1)return -1;//no TTL
    
    long t = ttl - ((new Date().getTime() - lastAccess.getTime())/1000);
    if(t < 0) return 0;
    return t;
  }
  
}
