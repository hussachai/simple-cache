package com.siberhus.zappos.usecase;

import java.util.Date;

/**
 * 
 * @author Zappos
 * @author Hussachai
 */
public class FakeDBAccess {
  
  public Date getLastLoginForUser(String userId) {
    if (Math.random() < .5)
      return new Date(System.currentTimeMillis());
    return new Date(System.currentTimeMillis() - 42 * 60 * 60 * 1000);
  }
  
  public void setLastLoginForUser(String userId, Date date) throws DBEx {
    // do nothing
  }
}

class DBEx extends RuntimeException{
  
  private static final long serialVersionUID = 1L;
  
}