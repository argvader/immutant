/*
 * Copyright 2008-2012 Red Hat, Inc, and individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.immutant.core;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import clojure.lang.RT;
import clojure.lang.Var;

/** 
 * Utils for bootstrapping a clojure application. Any Vars invoked here will 
 * be in the global runtime, and not in the application runtime.
 * It's *extremely* easy to break runtime isolation when using these methods. 
 * I'd avoid calling any of them after Phase.STRUCTURE, as that will void your 
 * warranty.
 */
public class ApplicationBootstrapUtils {
    
    /**
     * Parses the given deployment descriptor and returns the resulting Map with all of the keys stringified.
     * See bootstrap.clj.
     */
    @SuppressWarnings("rawtypes")
    public static Map parseDescriptor(final File file) throws Exception {
        return (Map) inCL( new Callable() {
            public Object call() throws Exception {
                return bootstrapVar( "read-and-stringify-descriptor" ).invoke( file ); 
            }
        } );
    }

    @SuppressWarnings("rawtypes")
    public static Map readFullAppConfig(final File descriptor, final File applicationRoot) throws Exception {
        return (Map) inCL( new Callable() {
            public Object call() throws Exception {
                return bootstrapVar( "read-and-stringify-full-app-config" ).invoke( descriptor, applicationRoot ); 
            }
        } );
    }
    
    @SuppressWarnings("rawtypes")
    public static String readFullAppConfigAsString(final File descriptor, final File applicationRoot) throws Exception {
        return (String) inCL( new Callable() {
            public Object call() throws Exception {
                return bootstrapVar( "read-full-app-config-to-string" ).invoke( descriptor, applicationRoot ); 
            }
        } );
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<String> resourceDirs(final File applicationRoot, final List profiles) throws Exception {
        return (List<String>) inCL( new Callable() {
            public Object call() throws Exception {
                return bootstrapVar( "resource-paths" ).invoke( applicationRoot, profiles ); 
            }
        } );
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static List<File> getDependencies(final File applicationRoot, final boolean resolveDeps, final List profiles) throws Exception {
        return (List<File>) inCL( new Callable() {
            public Object call() throws Exception {
                return bootstrapVar( "get-dependencies" ).invoke( applicationRoot, resolveDeps, profiles ); 
            }
        } );
    }
    
    private static Var bootstrapVar(String varName) throws Exception {
        RT.load( "immutant/runtime/bootstrap" ); //FIXME: this doesn't need to be loaded on every call
        return RT.var( "immutant.runtime.bootstrap", varName );
    }

    @SuppressWarnings("rawtypes")
    private static Object inCL(Callable body) throws Exception {
        ClassLoader originalCl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader( Var.class.getClassLoader() );
            return body.call();
        } finally {
            Thread.currentThread().setContextClassLoader( originalCl );
        }
    }
}
