package com.siberhus.zappos.cache;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import com.siberhus.zappos.cache.utils.Config;
import com.siberhus.zappos.cache.utils.ConfigKeys;

public class LRUCacheTest {
  
  private Config config(){
    Properties props = new Properties();
    props.setProperty(ConfigKeys.Cache.MAX_ITEM, "3");
    props.setProperty(ConfigKeys.Cache.DISK_STORE, "cache");
    return new Config(props);
  }
  
  @Test
  public void testSetGet(){
    Cache c = new LRUCache("setget", config());
    c.set("1", new Element("1"));
    c.set("2", new Element("2"));
    c.set("3", new Element("3"));
    c.set("4", new Element("4"));
    c.set("5", new Element("5"));
    Assert.assertEquals("2", c.get("2").getValue());
    Assert.assertEquals("1", c.get("1").getValue());
    Assert.assertNull(c.get("0"));
  }
  
  @Test
  public void testTTL()throws Exception {
    Cache c = new LRUCache("ttl", config());
    c.set("1", new Element("1").ttl(1, TimeUnit.SECONDS));
    Assert.assertNotNull(c.get("1"));
    Thread.sleep(1000);
    Assert.assertNull(c.get("1"));
    
    c.set("2", new Element("2").ttl(3, TimeUnit.SECONDS));
    Element e = c.get("2");
    while(e.getTTL() != 0){
      System.out.println("TTL = " + e.getTTL());
      Thread.sleep(500);
    }
    Assert.assertNull(c.get("2"));
    System.out.println();
  }
  
  @Test
  public void testEvict() throws Exception {
    Cache c = new LRUCache("setget", config());
    c.set("1", new Element("1"));
    c.set("2", new Element("2"));
    c.set("3", new Element("3"));
    c.set("4", new Element("4"));
    c.set("5", new Element("5"));
    Assert.assertEquals("2", c.get("2").getValue());
    Assert.assertEquals("1", c.get("1").getValue());
    
    c.evict("1");
    Assert.assertNull(c.get("1"));
    c.evict("5");
    Assert.assertNull(c.get("5"));
  }
}
