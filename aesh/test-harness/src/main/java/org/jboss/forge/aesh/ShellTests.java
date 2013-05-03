/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.aesh;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jboss.forge.container.exception.ContainerException;

/**
 * Utilities for testing shell interactions.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class ShellTests
{
   public static void waitForCallback(ByteArrayOutputStream stream, int quantity, TimeUnit unit)
            throws TimeoutException
   {
      stream.reset();
      long start = System.currentTimeMillis();
      while (stream.toByteArray().length == 0)
      {
         if (System.currentTimeMillis() > (start + TimeUnit.MILLISECONDS.convert(quantity, unit)))
         {
            throw new TimeoutException("Timeout expired waiting for shell to respond [" + stream + "].");
         }

         try
         {
            Thread.sleep(10);
         }
         catch (InterruptedException e)
         {
            throw new ContainerException("Stream [" + stream + "] did not respond.", e);
         }
      }
   }
}
