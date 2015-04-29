package com.siberhus.zappos;

import java.util.Calendar;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.siberhus.zappos.cache.Cache;
import com.siberhus.zappos.cache.Element;
import com.siberhus.zappos.cache.LRUCache;
import com.siberhus.zappos.cache.utils.Config;
import com.siberhus.zappos.cache.utils.ConfigKeys;
import com.siberhus.zappos.usecase.LoginService;
import com.siberhus.zappos.usecase.LoginServiceImpl;
import com.siberhus.zappos.usecase.UserLoginTimestamp;

/**
 * 
 * @author Hussachai
 *
 */
public class UseCaseTest {
  
  private Config config(){
    Properties props = new Properties();
    props.setProperty(ConfigKeys.Cache.MAX_ITEM, "100");
    props.setProperty(ConfigKeys.Cache.DISK_STORE, "cache");
    return new Config(props);
  }
  
  @Test
  public void integratedTest(){
    Cache cache = new LRUCache("user", config());
    
    LoginService service = new LoginServiceImpl(cache);
    for(int i = 0; i < 100; i++){
      service.userJustLoggedIn(String.valueOf(i));
    }
    Calendar last2Days = Calendar.getInstance();
    last2Days.set(Calendar.DAY_OF_YEAR, last2Days.get(Calendar.DAY_OF_YEAR) - 2); 
    for(int i = 100; i < 200; i++){
      cache.set(String.valueOf(i), new Element(
          new UserLoginTimestamp(String.valueOf(i), last2Days.getTime())));
    }
    
    int count = 0;
    for(int i = 0; i < 200; i++){
      if(service.hasUserLoggedInWithin24(String.valueOf(i))) count ++;
    }
    
    /* All IDs must be in cache 
     * Total 200 users in cache but half of them logged in within 24 hrs. 
     */
    Assert.assertEquals(100, count);
    
    count = 0;
    /* Try IDs that are not in cache */
    for(int i = 200; i < 300; i++){
      if(service.hasUserLoggedInWithin24(String.valueOf(i))) count ++;
    }
    /* Should be somewhere around 0-100 because DB returns random result */
    Assert.assertTrue(count >= 0 && count <= 100);
    System.out.println(count);
  }
  
}
