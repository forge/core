package org.jboss.forge.addon.shell.aesh.completion;

import java.io.File;

import org.jboss.aesh.cl.completer.FileOptionCompleter;
import org.jboss.aesh.cl.completer.OptionCompleter;
import org.jboss.aesh.util.FileLister.Filter;
import org.jboss.forge.addon.convert.ConverterFactory;
import org.jboss.forge.addon.resource.FileResource;
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
   public static OptionCompleter getCompletionFor(InputComponent<?, Object> component, ShellContext context,
            ConverterFactory converterFactory)
   {
      UISelection<FileResource<?>> selection = context.getInitialSelection();
      final File cwd = selection.isEmpty() ? OperatingSystemUtils.getUserHomeDir() : selection.get()
               .getUnderlyingResourceObject();
      InputType inputType = component.getFacet(HintsFacet.class).getInputType();
      OptionCompleter strategy = null;
      if (inputType == InputType.FILE_PICKER && cwd.isDirectory())
      {
         strategy = new FileOptionCompleter(cwd);
      }
      else if (inputType == InputType.DIRECTORY_PICKER && cwd.isDirectory())
      {
         strategy = new FileOptionCompleter(cwd, Filter.DIRECTORY);
      }
      else if (inputType == InputType.CHECKBOX || Boolean.class
               .isAssignableFrom(component.getValueType()))
      {
         strategy = null;
      }
      else if (component instanceof SelectComponent)
      {
         strategy = new SelectComponentOptionCompleter((SelectComponent<?, Object>) component, converterFactory);
      }
      // Always try UICompleter first and then fallback to the chosen strategy
      strategy = new UICompleterOptionCompleter(strategy, context, component, converterFactory);
      return strategy;
   }
}
