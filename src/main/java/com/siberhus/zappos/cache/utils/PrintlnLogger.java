package com.siberhus.zappos.cache.utils;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * Simple system.out logger
 * 
 * @author Hussachai
 *
 */
public enum PrintlnLogger {

  INSTANCE;

  enum LogLevel {
    ERROR, WARN, INFO, DEBUG
  }

  private LogLevel threshold = LogLevel.DEBUG;
  private boolean enabled = true;

  /**
   * 
   */
  private PrintlnLogger() {
    String enabled = System.getProperty("log.enabled");
    if ("false".equalsIgnoreCase(enabled)) {
      this.enabled = false;
    }
    String level = System.getProperty("log.level");
    if (level != null) {
      try {
        this.threshold = LogLevel.valueOf(level.trim().toUpperCase());
      } catch (Exception e) {
        System.out.println("[WARN] Unrecognized log level!");
      }
    }
  }

  /**
   * log error
   * 
   * @param message
   * @param params
   */
  public void error(String message, Object... params) {
    println(LogLevel.ERROR, message, params);
  }

  /**
   * log error and print stacktrace
   * 
   * @param e
   * @param message
   */
  public void error(Throwable e, String message) {
    if (println(LogLevel.ERROR, message)) {
      e.printStackTrace();
    }
  }

  /**
   * log warning
   * 
   * @param message
   * @param params
   */
  public void warn(String message, Object... params) {
    println(LogLevel.WARN, message, params);
  }

  /**
   * log warning and print stacktrace
   * 
   * @param e
   * @param message
   */
  public void warn(Throwable e, String message) {
    if (println(LogLevel.WARN, message)) {
      e.printStackTrace();
    }
  }

  /**
   * log info
   * 
   * @param message
   * @param params
   */
  public void info(String message, Object... params) {
    println(LogLevel.INFO, message, params);
  }

  /**
   * log debug
   * 
   * @param message
   * @param params
   */
  public void debug(String message, Object... params) {
    println(LogLevel.DEBUG, message, params);
  }

  /**
   * 
   * @param message
   * @param params
   */
  public void println(String message, Object... params) {
    System.out.println(formatMessage(message, params));
  }

  /**
   * Format the string The ' will be escaped automatically To escape {}
   * placeholder, use {{}}
   * 
   * @param message
   * @param params
   * @return
   */
  public static String formatMessage(String message, Object... params) {
    return MessageFormat.format(message.replace("'", "''").replace("{{", "'{'")
        .replace("}}", "'}'"), params);
  }

  /**
   * 
   * @param level
   * @param message
   * @param params
   * @return
   */
  private boolean println(LogLevel level, String message, Object... params) {
    if (enabled && threshold.ordinal() >= level.ordinal()) {
      SimpleDateFormat sdf = new SimpleDateFormat("MMM dd@HH:mm:ss");
      message = sdf.format(new Date()) + " [" + level.name() + "] " + message;
      println(message, params);
      return true;
    }
    return false;
  }

}