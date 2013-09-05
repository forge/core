/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.aesh.cl.exception.CommandLineParserException;
import org.jboss.aesh.cl.parser.ParsedCompleteObject;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.shell.ui.ShellUIBuilderImpl;
import org.jboss.forge.addon.shell.util.ShellUtil;
import org.jboss.forge.addon.ui.UICommand;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.result.Result;

/**
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public abstract class AbstractShellInteraction implements Comparable<AbstractShellInteraction>
{
   private final String name;
   private final ShellContext context;
   private final UICommand root;

   protected final CommandLineUtil commandLineUtil;

   protected AbstractShellInteraction(UICommand root, ShellContext shellContext,
            CommandLineUtil commandLineUtil)
   {
      this.root = root;
      this.name = ShellUtil.shellifyName(root.getMetadata().getName());
      this.context = shellContext;
      this.commandLineUtil = commandLineUtil;
   }

   public abstract Map<String, InputComponent<?, Object>> getInputs();

   public abstract List<String> getCompletionOptions(String typed, String line);

   public abstract ParsedCompleteObject parseCompleteObject(String line) throws CommandLineParserException;

   public abstract void populateInputs(String line, boolean lenient) throws CommandLineParserException;

   public boolean hasArguments()
   {
      boolean result = false;
      for (String name : getInputs().keySet())
      {
         if (name.isEmpty())
         {
            result = true;
            break;
         }
      }
      return result;
   }

   /**
    * Returns the error messages
    * 
    * @return
    */
   public abstract List<String> validate();

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

   protected void removeExistingOptions(String commandLine, Iterable<String> availableOptions)
   {
      Iterator<String> it = availableOptions.iterator();
      while (it.hasNext())
      {
         if (commandLine.contains(it.next()))
         {
            it.remove();
         }
      }
   }

}