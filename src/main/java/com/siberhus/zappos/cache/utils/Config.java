package com.siberhus.zappos.cache.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

/**
 * Simple configuration. Thin wrap over java.uitl.Properties.
 * 
 * @author Hussachai
 *
 */
public class Config {

  private Properties props;
  
  public Config(){
    props = new Properties();
  }
  
  public Config(Properties props){
    if(props == null){
      throw new IllegalArgumentException("props cannot be null");
    }
    this.props = props;
  }
  
  public void load(InputStream in) throws IOException{
    props.load(in);
  }
  
  public void load(Reader reader) throws IOException{
    props.load(reader);
  }
  
  public int size(){
    return props.size();
  }
  
  public Set<Entry<Object, Object>> entrySet(){
    return props.entrySet();
  }
  
  public String getString(String key){
    return props.getProperty(key);
  }
  
  public String getString(String key, String defaultValue){
    return props.getProperty(key, defaultValue);
  }
  
  public Integer getInt(String key){
    return getInt(key, null);
  }
  
  public Integer getInt(String key, Integer defaultValue){
    String strVal = props.getProperty(key);
    if(strVal != null){
      return Integer.parseInt(strVal);
    }
    return defaultValue;
  }
  
}
