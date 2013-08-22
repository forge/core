/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.commands;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.shell.Shell;
import org.jboss.forge.addon.shell.ui.AbstractShellCommand;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.shell.util.PathspecParser;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.input.UIInputMany;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * Changes to the current directory
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class CdCommand extends AbstractShellCommand
{
   @Inject
   ResourceFactory resourceFactory;

   @Inject
   @WithAttributes(label = "Arguments")
   private UIInputMany<String> arguments;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(arguments);
   }

   @Override
   public Metadata getMetadata()
   {
      return super.getMetadata().name("cd").description("Change the current directory");
   }

   @Override
   public Result execute(ShellContext context) throws Exception
   {
      Shell shell = context.getProvider();
      FileResource<?> currentResource = shell.getCurrentResource();
      Iterator<String> it = arguments.getValue().iterator();
      final Result result;
      if (it.hasNext())
      {
         String newPath = it.next();
         final List<Resource<?>> newResource = new PathspecParser(resourceFactory, currentResource, newPath).resolve();
         if (newResource.isEmpty() || !newResource.get(0).exists())
         {
            result = Results.fail(newPath + ": No such file or directory");
         }
         else
         {
            FileResource<?> newFileResource = newResource.get(0).reify(FileResource.class);
            if (newFileResource == null)
            {
               result = Results.fail(newPath + ": Invalid path");
            }
            else
            {
               shell.setCurrentResource(newFileResource);
               result = Results.success();
            }
         }
      }
      else
      {
         result = Results.success();
      }
      return result;
   }
}
