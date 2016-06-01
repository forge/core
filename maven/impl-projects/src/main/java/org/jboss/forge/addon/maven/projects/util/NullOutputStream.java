/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.util;

import java.io.OutputStream;

/**
 * Implementation of {@link OutputStream} that sends all written bytes to /dev/null
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public final class NullOutputStream extends OutputStream
{
   @Override
   public void write(int b)
   {
      // bye!
   }

   @Override
   public void write(byte[] b, int off, int len)
   {
      // bye!
   }
}
