/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.templates;

import java.io.IOException;
import java.io.Writer;

import org.jboss.forge.addon.resource.Resource;

/**
 * A representation of a {@link Template} that knows how to render a specific {@link Resource} instance.
 * 
 * @author Vineet Reynolds
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface Template
{
   /**
    * Fetches the underlying {@link Resource} associated with this {@link Template} instance.
    */
   Resource<?> getResource();

   /**
    * Process the given model and return a {@link String} result containing {@link Template} output.
    */
   public String process(Object model) throws IOException;

   /**
    * Process the given model and write the {@link Template} output to the given {@link Writer}.
    */
   public void process(Object model, Writer output) throws IOException;
}
