package com.siberhus.zappos.usecase;

/**
 * 
 * @author Zappos
 *
 */
public interface LoginService {
  /**
   * Checks whether the user has logged in within the last 24 hours does NOT
   * imply a login
   * 
   * @param userId
   * @return True if the user HAS logged in within 24 hours False otherwise
   */
  public boolean hasUserLoggedInWithin24(String userId);
  
  /**
   * Sets the last login time for the user to now.
   * 
   * @param userId
   */
  public void userJustLoggedIn(String userId);
}
