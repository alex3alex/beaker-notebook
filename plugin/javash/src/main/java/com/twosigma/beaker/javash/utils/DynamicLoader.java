/*
 *  Copyright 2014 TWO SIGMA OPEN SOURCE, LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.twosigma.beaker.javash.utils;


import org.xeustechnologies.jcl.ProxyClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import org.xeustechnologies.jcl.exception.JclException;
import org.xeustechnologies.jcl.exception.ResourceNotFoundException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

class DynamicLoader {
    protected final Map<String, Class> classes;
    private final String dirPath;
    private final DynamicLoaderProxy dlp = new DynamicLoaderProxy();
    private SubClassLoader subLoader;
    private final ClassLoader parent;
    
    public DynamicLoader(ClassLoader p, String dir) {
        classes = Collections.synchronizedMap( new HashMap<String, Class>() );
        dirPath = dir;
        parent = p;
        subLoader = new SubClassLoader(p);
    }

    class SubClassLoader extends ClassLoader {
        public SubClassLoader(ClassLoader p) {
            super(p);
        }
        public Class my_defineClass(String s, byte [] b, int a, int x) {
            return defineClass(s,b,a,x);
        }
        public void my_definePackage(String n) {
            definePackage( n, null, null, null, null, null, null, null );
        }
        public void my_resolveClass(Class r) {
            resolveClass(r);
        }
    }
    
    public DynamicLoaderProxy getProxy() { return dlp; }
    
    class DynamicLoaderProxy extends ProxyClassLoader {

        public DynamicLoaderProxy() {
            order = 10;
            enabled = true;
        }

        @Override
        public Class loadClass(String className, boolean resolveIt) {
            Class result = null;
            byte[] classBytes;

            result = classes.get( className );
            if (result != null) {
                System.err.println("class "+className+" found in cache.");
                return result;
            }

            classBytes = loadClassBytes( className );
            if (classBytes == null) {
                System.err.println("class "+className+" load class bytes failed.");
                return null;
            }

            result = subLoader.my_defineClass( className, classBytes, 0, classBytes.length );
            if (result == null) {
                System.err.println("class "+className+" define class failed.");
                return null;
            }

            /*
             * Preserve package name.
             */
            if (result.getPackage() == null) {
                int lastDotIndex = className.lastIndexOf( '.' );
                String packageName = (lastDotIndex >= 0) ? className.substring( 0, lastDotIndex) : "";
                subLoader.my_definePackage( packageName);
            }

            if (resolveIt)
                subLoader.my_resolveClass( result );

            classes.put( className, result );
            System.err.println("class "+className+" loaded.");
            return result;
        }

        @Override
        public InputStream loadResource(String name) {
            System.err.println("load Resource "+name);
            //byte[] arr = classpathResources.getResource( name );
            //if (arr != null) {
            //    return new ByteArrayInputStream( arr );
            //}

            return null;
        }

        @Override
        public URL findResource(String name) {
            System.err.println("find Resource "+name);
            //URL url = classpathResources.getResourceURL( name );
            //if (url != null) {
            //    return url;
            //}

            return null;
        }
    }
    
    
    protected byte[] loadClassBytes(String className) {
        String path = dirPath + "/" + className.replace(".", "/") + ".class";
        
        System.err.println("path is "+path);
        
        File f = new File(path);
        if (f.exists()) {
            byte [] content = new byte[(int) f.length()];
            FileInputStream fis = null;
            try {
                fis = new FileInputStream( f );
                if (fis.read( content ) != -1) {
                    return content;
                }
            } catch (IOException e) {
                throw new JclException( e );
            } finally {
                if (fis != null)
                    try {
                        fis.close();
                    } catch (IOException e) {
                        throw new JclException( e );
                    }
            }
            
        }
        return null;
    }

    public void clearCache() {
        classes.clear();
        subLoader = new SubClassLoader(parent);
    }
}