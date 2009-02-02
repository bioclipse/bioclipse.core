/*******************************************************************************
 *Copyright (c) 2008 The Bioclipse Team and others.
 *All rights reserved. This program and the accompanying materials
 *are made available under the terms of the Eclipse Public License v1.0
 *which accompanies this distribution, and is available at
 *http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package net.bioclipse.data;

import java.io.IOException;

import net.bioclipse.core.business.BioclipseException;



public class CopyTools {


    /** Creates new Copy */
    private CopyTools() {
    }

    public static void copy(java.io.File destination, java.io.File source) throws BioclipseException {
        if (source.isDirectory()) {
            if (!destination.isDirectory()) {
                throw new BioclipseException("Destination '"+destination.getName()+"' is not directory.");
            }
            copyDirectory(destination,source);
        } else {
            if (destination.isDirectory()) {
                destination=new java.io.File(destination,source.getName());
            }
            copyFile(destination,source);
        }
    }
    
    protected static void copyDirectory(java.io.File destination, java.io.File source) throws BioclipseException {
        java.io.File[] list=source.listFiles();
        for (int i=0;i<list.length;i++) {
            java.io.File dest=new java.io.File(destination,list[i].getName());
            if (list[i].isDirectory()) {
                dest.mkdir();
                copyDirectory(dest,list[i]);
            } else {
                copyFile(dest,list[i]);
            }
        }
    }
    
    protected static void copyFile(java.io.File destination, java.io.File source) throws BioclipseException {
    	java.io.FileInputStream inStream = null;
    	java.io.FileOutputStream outStream = null;
    	try {
            inStream=new java.io.FileInputStream(source);
            outStream=new java.io.FileOutputStream(destination);

            int len;
            byte[] buf=new byte[2048];
             
            while ((len=inStream.read(buf))!=-1) {
                outStream.write(buf,0,len);
            }
        } catch (Exception e) {
            throw new BioclipseException("Can't copy file "+source+" -> "+destination+": " + e.getMessage());
        } finally {
        	try {
				inStream.close();
	        	outStream.flush();
	        	outStream.close();
			} catch (IOException e) {
				throw new RuntimeException( e );
			}
        }
    }
}