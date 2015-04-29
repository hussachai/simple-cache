package com.siberhus.zappos.usecase;

import java.util.Date;

import com.siberhus.zappos.cache.Cache;
import com.siberhus.zappos.cache.Element;

/**
 * 
 * @author Hussachai
 *
 */
public class LoginServiceImpl implements LoginService {

  private FakeDBAccess db = new FakeDBAccess();
  
  private Cache cache;
  
  private final long HR24 = 1000*60*60*24;
  
  public LoginServiceImpl(Cache cache){
    this.cache = cache;
  }
  
  @Override
  public boolean hasUserLoggedInWithin24(String userId) {
    Element element = (Element)cache.get(userId);
    /* Error from cache or db should be propagated to the caller */
    Date lastAccess = null; 
    if(element != null){
      lastAccess = ((UserLoginTimestamp)element.getValue()).getLastAccess();
    }else{
      lastAccess = db.getLastLoginForUser(userId);
      cache.set(userId, new Element(new UserLoginTimestamp(userId, lastAccess)));
    }
    boolean in24 = (new Date().getTime() - lastAccess.getTime()) <= HR24 ;
    if(!in24 && element != null){
      /* Not in 24 hrs and a user is in cache */
      cache.evict(userId);
    }
    return in24;
  }
  
  @Override
  public void userJustLoggedIn(String userId) {
    Date timestamp = new Date();
    /* Save to db before cache because if error occurs, the program
     * should show error to users or redirect user to previous page and
     * let them try again.
     * If saving is successful, user&time will be saved to cache.
     * */
    db.setLastLoginForUser(userId, timestamp);
    cache.set(userId, new Element(
        new UserLoginTimestamp(userId, timestamp)));
  }
  
}
