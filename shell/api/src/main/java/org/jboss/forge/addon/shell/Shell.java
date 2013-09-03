/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell;

import java.io.Closeable;

import org.jboss.aesh.console.AeshConsole;
import org.jboss.aesh.console.Console;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.UIProvider;

/**
 * The command line shell.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface Shell extends UIProvider, Closeable
{
   /**
    * Get the native {@link Console} object.
    */
   public AeshConsole getConsole();

   /**
    * Sets the current working directory
    * 
    * @param resource should be a {@link FileResource}
    * @throws IllegalArgumentException if resource is null
    */
   public void setCurrentResource(FileResource<?> resource);

   /**
    * Returns the current working directory.
    */
   public FileResource<?> getCurrentResource();

   /**
    * Disposes this Shell object
    */
   @Override
   public void close();
}
