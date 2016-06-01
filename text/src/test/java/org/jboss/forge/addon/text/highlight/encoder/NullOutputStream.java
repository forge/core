/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.text.highlight.encoder;

import java.io.IOException;
import java.io.OutputStream;

public class NullOutputStream extends OutputStream
{
   public static final NullOutputStream INSTANCE = new NullOutputStream();

   private NullOutputStream()
   {
   }

   public void write(int b) throws IOException
   {
   }
}
