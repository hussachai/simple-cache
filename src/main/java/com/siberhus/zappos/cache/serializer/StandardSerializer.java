package com.siberhus.zappos.cache.serializer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.siberhus.zappos.cache.exception.InternalErrorException;
import com.siberhus.zappos.cache.exception.ReadWriteException;

/**
 * 
 * @author Hussachai
 *
 * @param <T>
 */
public class StandardSerializer extends FileBasedObjectSerializer {
  
  /**
   * 
   */
  @Override
  public CacheEntry readFromFile(File file) {
    try(ObjectInputStream reader = new ObjectInputStream(
        new BufferedInputStream(new FileInputStream(file)))){
      return (CacheEntry)reader.readObject();
    }catch(FileNotFoundException e){
      return null;
    }catch(IOException e){
      throw new ReadWriteException(e);
    }catch(ClassNotFoundException e){
      throw new InternalErrorException(e);
    }
  }
  
  /**
   * 
   */
  @Override
  public void writeToFile(File file, CacheEntry entry)  {
    try (ObjectOutputStream writer = new ObjectOutputStream(
        new BufferedOutputStream(new FileOutputStream(file)))){
      writer.writeObject(entry);
    }catch(IOException e){
      throw new ReadWriteException(e);
    }
  }
  
}
