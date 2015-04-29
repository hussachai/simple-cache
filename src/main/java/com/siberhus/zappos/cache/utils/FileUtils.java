package com.siberhus.zappos.cache.utils;

import java.io.File;

/**
 * 
 * @author Hussachai
 *
 */
public class FileUtils {

  /**
   * Recursively delete directories and files
   * @param file
   */
  public static void deleteFiles(File file){
    if (file.isDirectory()) {
      for (File c : file.listFiles())
        deleteFiles(c);
    }
    file.delete();
  }
  
  /**
   * 
   * @param fileName
   * @return
   */
  public static String ensureEndingSeparater(String fileName){
    if(fileName.endsWith("/") || fileName.endsWith(File.separator)){
      return fileName;
    }
    return fileName + File.separator;
  }
  
}
