package com.siberhus.zappos.cache.serializer;

import java.io.Serializable;

import com.siberhus.zappos.cache.Element;

/**
 * 
 * @author Hussachai
 *
 */
public class CacheEntry implements Serializable{
  
  private static final long serialVersionUID = -5854865033047737892L;

  private String key;
  
  private Element element;
  
  public CacheEntry(String key, Element element){
    this.key = key;
    this.element = element;
  }
  
  @Override
  public String toString(){
    return key;
  }
  
  public String getKey() {
    return key;
  }
  
  public Element getElement() {
    return element;
  }
  
}
