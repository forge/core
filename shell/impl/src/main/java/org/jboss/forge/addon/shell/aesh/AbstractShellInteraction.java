/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh;

import org.jboss.aesh.cl.parser.CommandLineParser;
import org.jboss.forge.addon.shell.ShellImpl;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.shell.ui.ShellUIPromptImpl;
import org.jboss.forge.addon.shell.util.ShellUtil;
import org.jboss.forge.addon.ui.controller.CommandController;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;

/**
 * Base class for commands and wizards running in shell
 * 
 * @see {@link ShellSingleCommand} and {@link ShellWizard}
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class AbstractShellInteraction implements Comparable<AbstractShellInteraction>
{
   protected static final String INTERACTIVE_MODE_MESSAGE = "Required inputs not satisfied, entering interactive mode";
   protected static final String NON_INTERACTIVE_MODE_MESSAGE = "Required inputs not satisfied and INTERACTIVE=false, aborting. Please 'export INTERACTIVE=true' or try again providing the required values.";

   private final String name;
   private final CommandController controller;
   private final UICommandMetadata metadata;
   protected final CommandLineUtil commandLineUtil;
   private final ShellContext context;

   protected AbstractShellInteraction(CommandController controller, ShellContext shellContext,
            CommandLineUtil commandLineUtil)
   {
      this.context = shellContext;
      this.controller = controller;
      this.metadata = controller.getMetadata();
      this.name = ShellUtil.shellifyCommandName(metadata.getName());
      this.commandLineUtil = commandLineUtil;
   }

   protected abstract CommandLineParser<?> getParser(ShellContext shellContext, String completeLine,
            CommandAdapter command) throws Exception;

   /**
    * Called when a required input value is missing.
    * 
    * @return true if the operation succeeded, false otherwise
    * @throws InterruptedException if Ctrl+C or Ctrl+D was pressed during the input
    */
   protected abstract boolean promptRequiredMissingValues(ShellImpl impl) throws InterruptedException;

   protected ShellContext getContext()
   {
      return context;
   }

   protected CommandController getController()
   {
      return controller;
   }

   protected final String getName()
   {
      return name;
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

   protected boolean hasMissingRequiredInputValues(Iterable<InputComponent<?, ?>> inputs)
   {
      for (InputComponent<?, ?> input : inputs)
      {
         if (input.isEnabled())
         {
            if (input.getFacet(HintsFacet.class).isPromptInInteractiveMode())
            {
               return true;
            }
         }
      }
      return false;
   }

   /**
    * Called by {@link AbstractShellInteraction#promptRequiredMissingValues(ShellImpl)}
    */
   protected void promptRequiredMissingValues(ShellImpl shell, Iterable<InputComponent<?, ?>> inputs)
            throws InterruptedException
   {
      ShellUIPromptImpl prompt = shell.createPrompt(context);
      for (InputComponent<?, ?> input : inputs)
      {
         if (input.isEnabled() && !input.isDeprecated())
         {
            boolean requiredInputMissing = input.isRequired() && !(input.hasDefaultValue() || input.hasValue());
            Object obj = prompt.promptValueFrom(input);
            if (obj == null && requiredInputMissing)
            {
               // No value returned. Just stop testing other inputs
               break;
            }
         }
      }

   }

}