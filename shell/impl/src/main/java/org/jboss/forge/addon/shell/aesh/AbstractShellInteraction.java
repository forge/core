/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.jboss.aesh.cl.parser.CommandLineParser;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.shell.ui.ShellUIBuilderImpl;
import org.jboss.forge.addon.shell.ui.ShellValidationContext;
import org.jboss.forge.addon.shell.util.ShellUtil;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.furnace.util.Streams;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractShellInteraction implements Comparable<AbstractShellInteraction>
{
   private final String name;
   private final ShellContext context;
   private final UICommand root;
   private final UICommandMetadata metadata;
   protected final CommandLineUtil commandLineUtil;

   protected AbstractShellInteraction(UICommand root, ShellContext shellContext,
            CommandLineUtil commandLineUtil)
   {
      this.root = root;
      this.metadata = root.getMetadata(shellContext);
      this.name = ShellUtil.shellifyName(metadata.getName());
      this.context = shellContext;
      this.commandLineUtil = commandLineUtil;
   }

   public abstract CommandLineParser getParser(ShellContext shellContext, String completeLine) throws Exception;

   public abstract Map<String, InputComponent<?, Object>> getInputs();

   public abstract ShellValidationContext validate();

   public abstract Result execute() throws Exception;

   protected Map<String, InputComponent<?, Object>> buildInputs(UICommand command)
   {
      // Initialize UICommand
      ShellUIBuilderImpl builder = new ShellUIBuilderImpl(context);
      try
      {
         command.initializeUI(builder);
      }
      catch (Exception e)
      {
         throw new RuntimeException("Error while initializing command", e);
      }
      return builder.getComponentMap();
   }

   public UICommand getSourceCommand()
   {
      return root;
   }

   public final String getName()
   {
      return name;
   }

   public final ShellContext getContext()
   {
      return context;
   }

   public File getManLocation()
   {
      URL manLocation = metadata.getDocLocation();
      if (manLocation == null)
      {
         return null;
      }
      else
      {
         try
         {
            File tmpFile = File.createTempFile("mantmp", ".txt");
            tmpFile.deleteOnExit();
            FileOutputStream fos = null;
            try
            {
               fos = new FileOutputStream(tmpFile);
               Streams.write(manLocation.openStream(), fos);
            }
            finally
            {
               Streams.closeQuietly(fos);
            }
            return tmpFile;
         }
         catch (IOException ie)
         {
            throw new IllegalStateException("Error while fetching man page", ie);
         }
      }
   }

   @Override
   public int compareTo(AbstractShellInteraction o)
   {
      return getName().compareTo(o.getName());
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
         return true;
      if (!(o instanceof AbstractShellInteraction))
         return false;

      AbstractShellInteraction that = (AbstractShellInteraction) o;

      if (!getName().equals(that.getName()))
         return false;

      return true;
   }

   @Override
   public int hashCode()
   {
      return getName().hashCode();
   }

   @Override
   public String toString()
   {
      return getName();
   }

}