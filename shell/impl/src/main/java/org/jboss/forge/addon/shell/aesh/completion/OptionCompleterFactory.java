package org.jboss.forge.addon.shell.aesh.completion;

import java.io.File;

import org.jboss.aesh.cl.completer.FileOptionCompleter;
import org.jboss.aesh.cl.completer.OptionCompleter;
import org.jboss.aesh.console.command.completer.CompleterInvocation;
import org.jboss.aesh.util.FileLister.Filter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.shell.ui.ShellContext;
import org.jboss.forge.addon.ui.context.UISelection;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.SelectComponent;
import org.jboss.forge.furnace.util.OperatingSystemUtils;

/**
 * Returns the completion based on the input component
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class OptionCompleterFactory
{
   public static OptionCompleter<CompleterInvocation> getCompletionFor(InputComponent<?, ?> component,
            ShellContext context, ConverterFactory converterFactory)
   {
      UISelection<FileResource<?>> selection = context.getInitialSelection();

      // FIXME This should use the Resource API to allow completion of virtual resources.
      final File cwd = selection.isEmpty() ? OperatingSystemUtils.getUserHomeDir() : selection.get()
               .getUnderlyingResourceObject();

      InputType inputType = component.getFacet(HintsFacet.class).getInputType();
      OptionCompleter<CompleterInvocation> strategy = null;
      if (inputType == InputType.FILE_PICKER && cwd.isDirectory())
      {
         strategy = new FileOptionCompleter(Filter.ALL);
      }
      else if (inputType == InputType.DIRECTORY_PICKER && cwd.isDirectory())
      {
         strategy = new FileOptionCompleter(Filter.DIRECTORY);
      }
      else if (component instanceof SelectComponent)
      {
         strategy = new SelectComponentOptionCompleter((SelectComponent<?, Object>) component, converterFactory);
      }
      else if (Resource.class.isAssignableFrom(component.getValueType()))
      {
         // fall back to Resource completion.
         strategy = new FileOptionCompleter(Filter.ALL);
      }
      // Always try UICompleter first and then fallback to the chosen strategy
      strategy = new UICompleterOptionCompleter(strategy, context, component, converterFactory);
      return strategy;
   }
}
