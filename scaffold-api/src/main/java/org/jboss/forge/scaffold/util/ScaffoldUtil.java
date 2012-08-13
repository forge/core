/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.scaffold.util;

import java.io.InputStream;

import org.jboss.forge.resources.FileResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.ShellPrompt;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class ScaffoldUtil
{
   public static Resource<?> createOrOverwrite(final ShellPrompt prompt, final FileResource<?> resource,
            final InputStream contents,
            final boolean overwrite)
   {
      if (!resource.exists() || overwrite
               || prompt.promptBoolean("[" + resource.getFullyQualifiedName() + "] File exists, overwrite?"))
      {
         resource.createNewFile();
         resource.setContents(contents);
         return resource;
      }
      return null;
   }

   public static Resource<?> createOrOverwrite(final ShellPrompt prompt, final FileResource<?> resource,
            final String contents,
            final boolean overwrite)
   {
      if (!resource.exists() || overwrite
               || prompt.promptBoolean("[" + resource.getFullyQualifiedName() + "] File exists, overwrite?"))
      {
         resource.createNewFile();
         resource.setContents(contents);
         return resource;
      }
      return null;
   }

}
