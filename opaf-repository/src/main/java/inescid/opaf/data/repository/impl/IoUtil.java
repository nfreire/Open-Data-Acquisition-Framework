/* IoUtil.java - created on 27/05/2014, Copyright (c) 2011 The European Library, all rights reserved */
package inescid.opaf.data.repository.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.FileChannel;
import java.util.zip.GZIPOutputStream;

/**
 * Utilities for general I/O
 * 
 * @author Nuno Freire (nfreire@gmail.com)
 * @since 27/05/2014
 */
public class IoUtil {

    /**
     * @param serializedobject
     * @return desirialized object using java serialization
     */
    public static Object fromByteArray(byte[] serializedobject) {
        try {
            ByteArrayInputStream inStream=new ByteArrayInputStream(serializedobject);
            ObjectInputStream objIn=new ObjectInputStream(inStream);
            Object obj = objIn.readObject();
            objIn.close();
            return obj;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }        
    }
    /**
     * @param serializable
     * @return serialized to byte[] with java serialization
     */
    public static byte[] toByteArray(Object serializable) {
        try {
            ByteArrayOutputStream outArray=new ByteArrayOutputStream();
            ObjectOutputStream o=new ObjectOutputStream(outArray);
            o.writeObject(serializable);
            o.close();
            return outArray.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }        
    }

     /**
     * @param serializable
     * @return serialized to byte[]
     */
    public static byte[] toByteArrayCompressed(Object serializable) {
         try {
             ByteArrayOutputStream outArray=new ByteArrayOutputStream();
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(outArray);
             ObjectOutputStream o=new ObjectOutputStream(gzipOutputStream);
             o.writeObject(serializable);
             o.close();
             return outArray.toByteArray();
         } catch (IOException e) {
             throw new RuntimeException(e.getMessage(), e);
         }        
     }
    
    /**
     * @param source
     * @param dest
     * @throws IOException
     */
    public static void copyFileUsingFileChannels(File source, File dest)
            throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace(System.out);
        } finally {
            if (inputChannel!=null)
                inputChannel.close();
            if (outputChannel!=null)
                outputChannel.close();
        }
    }
}
