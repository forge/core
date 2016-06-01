/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.aesh.completion;

import java.io.File;

import org.jboss.aesh.cl.completer.FileOptionCompleter;
import org.jboss.aesh.cl.completer.OptionCompleter;
import org.jboss.aesh.console.command.completer.CompleterInvocation;
import org.jboss.aesh.io.filter.AllResourceFilter;
import org.jboss.aesh.io.filter.DirectoryResourceFilter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.SelectComponent;

/**
 * Returns the completion based on the input component
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@SuppressWarnings("unchecked")
public class OptionCompleterFactory
{
   public static OptionCompleter<CompleterInvocation> getCompletionFor(InputComponent<?, ?> component,
            ShellContext context, ConverterFactory converterFactory)
   {
      UISelection<Resource<?>> selection = context.getInitialSelection();
      Resource<?> selectedResource = selection.get();

      File cwd = null;
      if (selectedResource instanceof FileResource)
      {
         cwd = ((FileResource<?>) selectedResource).getUnderlyingResourceObject();
      }

      String inputType = component.getFacet(HintsFacet.class).getInputType();
      OptionCompleter<CompleterInvocation> strategy = null;
      // FIXME This should use the Resource API to allow completion of virtual resources.
      if (InputType.FILE_PICKER.equals(inputType) && (cwd != null && cwd.isDirectory()))
      {
         strategy = new FileOptionCompleter(new AllResourceFilter());
      }
      else if (InputType.DIRECTORY_PICKER.equals(inputType) && (cwd != null && cwd.isDirectory()))
      {
         strategy = new FileOptionCompleter(new DirectoryResourceFilter());
      }
      else if (component instanceof SelectComponent)
      {
         strategy = new SelectComponentOptionCompleter((SelectComponent<?, Object>) component, converterFactory);
      }
      else if (Resource.class.isAssignableFrom(component.getValueType()))
      {
         // fall back to Resource completion.
         strategy = new FileOptionCompleter(new AllResourceFilter());
      }
      // Always try UICompleter first and then fallback to the chosen strategy
      strategy = new UICompleterOptionCompleter(strategy, context, component, converterFactory);
      return strategy;
   }
}
