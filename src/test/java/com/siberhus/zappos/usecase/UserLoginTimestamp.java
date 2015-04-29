package com.siberhus.zappos.usecase;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author Hussachai
 *
 */
public class UserLoginTimestamp implements Serializable{
  
  private static final long serialVersionUID = 1L;
  
  private String userId;
  
  private Date lastAccess;

  public UserLoginTimestamp(){};
  
  public UserLoginTimestamp(String userId, Date lastAccess){
    this.userId = userId;
    this.lastAccess = lastAccess;
  }
  
  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public Date getLastAccess() {
    return lastAccess;
  }

  public void setLastAccess(Date lastAccess) {
    this.lastAccess = lastAccess;
  }
  
}
