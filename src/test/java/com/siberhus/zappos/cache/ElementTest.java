package com.siberhus.zappos.cache;

import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author Hussachai
 *
 */
public class ElementTest {

  @Test
  public void testTTL()throws Exception{
    
    Element e = new Element("something").ttl(3, TimeUnit.SECONDS);
    Assert.assertTrue(e.getTTL() > 0);
    Thread.sleep(3000);
    Assert.assertTrue(e.getTTL() == 0);
    Thread.sleep(1000);
    Assert.assertTrue(e.getTTL() == 0);
    
    e.touch();
    Assert.assertTrue(e.getTTL() > 2);
    
    e = new Element("foo", 3000);
    Assert.assertTrue(e.getTTL() > 2);
    
    e = new Element("bar");
    Assert.assertEquals(-1, e.getTTL());
  }
  
}
