/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.resource;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * A {@link WriteableResource} allows its contents to be changed
 * 
 * @param <T> The {@link Resource} type that implements this interface.
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface WriteableResource<T extends WriteableResource<T, R>, R> extends Resource<R>
{
   /**
    * Set the contents of this {@link WriteableResource} to the given {@link String} using UTF-8 encoding.
    */
   T setContents(String data);

   /**
    * Set the contents of this {@link WriteableResource} to the given {@link String} using the specified encoding.
    */
   T setContents(String data, Charset charset);

   /**
    * Set the contents of this {@link WriteableResource} to the given character array using UTF-8 encoding.
    */
   T setContents(char[] data);

   /**
    * Set the contents of this {@link WriteableResource} to the given character array using the specified encoding.
    */
   T setContents(char[] data, Charset charset);

   /**
    * Set the contents of this {@link WriteableResource} to the contents of the given {@link InputStream}.
    */
   T setContents(InputStream data);

   /**
    * Returns the {@link OutputStream} for this {@link WriteableResource}
    */
   OutputStream getResourceOutputStream();

   /**
    * Returns the {@link OutputStream} for this {@link WriteableResource}
    * 
    * @param append true if the {@link OutputStream} should append to the existing contents, false if it should
    *           overwrite
    */
   OutputStream getResourceOutputStream(boolean append);
}