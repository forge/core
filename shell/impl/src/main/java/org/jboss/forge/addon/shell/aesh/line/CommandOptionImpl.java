/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.shell.aesh.line;

import java.util.List;

import org.jboss.aesh.cl.internal.ProcessedOption;
import org.jboss.forge.addon.shell.line.CommandOption;

/**
 * Default {@link CommandOption} implementation
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
class CommandOptionImpl implements CommandOption
{
   private final ProcessedOption option;

   public CommandOptionImpl(ProcessedOption option)
   {
      this.option = option;
   }

   @Override
   public String getName()
   {
      return option.getName();
   }

   @Override
   public String getValue()
   {
      return option.getValue();
   }

   @Override
   public List<String> getValues()
   {
      return option.getValues();
   }

   @Override
   public String toString()
   {
      return "CommandOptionImpl [option=" + option + "]";
   }
}