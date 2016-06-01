/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.spi;

import java.io.Closeable;

/**
 * A SPI for Terminals
 *
 * NOTE: This interface may change in the future
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface Terminal extends Closeable
{
   /**
    * Initializes this terminal
    */
   void initialize();

   /**
    * Returns the width of the terminal (in pixels)
    */
   int getWidth();

   /**
    * Returns the height of the terminal (in pixels)
    */
   int getHeight();
}
