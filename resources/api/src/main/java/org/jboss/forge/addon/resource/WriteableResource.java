/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.resource;

import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * A {@link WriteableResource} allows its contents to be changed
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface WriteableResource<T extends WriteableResource<T>>
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

}