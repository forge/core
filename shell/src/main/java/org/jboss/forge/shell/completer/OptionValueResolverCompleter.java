/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.completer;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.ResourceFilter;
import org.jboss.forge.resources.ResourceFlag;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.PromptType;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.command.OptionMetadata;
import org.jboss.forge.shell.command.parser.CommandParserContext;
import org.jboss.forge.shell.util.BeanManagerUtils;
import org.jboss.forge.shell.util.JavaPathspecParser;
import org.jboss.forge.shell.util.PathspecParser;

public class OptionValueResolverCompleter implements CommandCompleter
{

   @Inject
   private Shell shell;

   @Inject
   private BeanManager manager;
   @Inject
   private ResourceFactory resourceFactory;

   @Override
   public void complete(final CommandCompleterState st)
   {
      PluginCommandCompleterState state = ((PluginCommandCompleterState) st);

      OptionMetadata option = state.getOption();
      if ((option != null) && (state.getCommandContext() != null))
      {

         CommandParserContext commandContext = state.getCommandContext();
         Map<OptionMetadata, Object> valueMap = commandContext.getValueMap();
         if (valueMap.isEmpty() || valueMap.containsKey(option))
         {
            if (option.hasCustomCompleter())
            {
               CommandCompleter completer = BeanManagerUtils.getContextualInstance(manager, option.getCompleterType());
               if (completer == null)
               {
                  throw new RuntimeException("Could not instantiate completer of type: "
                           + option.getCompleterType().getName()
                           + ", completers must be valid CDI beans (public top-level classes.)");
               }
               completer.complete(state);
            }
            else if (option.isEnum())
            {
               @SuppressWarnings("unchecked")
               EnumCompleter completer = new EnumCompleter((Class<Enum<?>>) option.getType());
               completer.complete(state);
            }
            else if (isJavaResourceAssignable(option))
            {
               completeJavaPaths(state, option, valueMap, new ResourceFilter()
               {
                  @Override
                  public boolean accept(final Resource<?> resource)
                  {
                     return ((resource instanceof DirectoryResource) || (resource instanceof JavaResource));
                  }
               });
            }
            else if (isJavaPackageAssignable(option))
            {
               completeJavaPaths(state, option, valueMap, new ResourceFilter()
               {
                  @Override
                  public boolean accept(final Resource<?> resource)
                  {
                     return (resource instanceof DirectoryResource);
                  }
               });
            }
            else if (isFileResourceAssignable(option))
            {
               completeFilenames(state, option, valueMap);
            }
         }
      }
   }

   private void completeJavaPaths(final PluginCommandCompleterState state, final OptionMetadata option,
            final Map<OptionMetadata, Object> valueMap, final ResourceFilter filter)
   {
      Project project = shell.getCurrentProject();
      if ((project != null) && project.hasFacet(JavaSourceFacet.class))
      {
         ArrayList<String> results = new ArrayList<String>();
         String[] values;

         if (valueMap.isEmpty())
         {
            values = new String[] { "" };
         }
         else if (valueMap.get(option) instanceof String[])
         {
            values = (String[]) valueMap.get(option);
         }
         else if (valueMap.get(option) == null)
         {
            values = new String[] { "" };
         }
         else
         {
            values = new String[] { String.valueOf(valueMap.get(option)) };
         }

         String val = values[values.length - 1];
         for (Resource<?> r : new JavaPathspecParser(project.getFacet(JavaSourceFacet.class), val + "*")
                  .resolve(filter))
         {
            // Add result to the results list, and append a '.' if the
            // resource has children.
            String name = ("~".equals(val) ? "~." : "") + r.getName()
                     + ((r.isFlagSet(ResourceFlag.Node) && !r.listResources(filter).isEmpty()) ? "." : "");
            results.add(name);
         }

         // Record the current index point in the buffer. If we're at
         // the separator char
         // set the value ahead by 1.
         int lastNest = val.lastIndexOf(".");
         state.setIndex((state.getOriginalIndex() - val.length()) + (lastNest != -1 ? lastNest + 1 : 0));

         state.getCandidates().addAll(results);
      }
   }

   private boolean isJavaPackageAssignable(final OptionMetadata option)
   {
      return PromptType.JAVA_PACKAGE.equals(option.getPromptType());
   }

   private boolean isJavaResourceAssignable(final OptionMetadata option)
   {
      return JavaResource[].class.isAssignableFrom(option.getBoxedType())
               || JavaResource.class.isAssignableFrom(option.getBoxedType())
               || PromptType.JAVA_CLASS.equals(option.getPromptType());
   }

   private void completeFilenames(final PluginCommandCompleterState state, final OptionMetadata option,
            final Map<OptionMetadata, Object> valueMap)
   {
      ArrayList<String> results = new ArrayList<String>();
      String[] values;

      if (valueMap.isEmpty())
      {
         values = new String[] { "" };
      }
      else if (valueMap.get(option) instanceof String[])
      {
         values = (String[]) valueMap.get(option);
      }
      else if (valueMap.get(option) == null)
      {
         values = new String[] { "" };
      }
      else
      {
         values = new String[] { String.valueOf(valueMap.get(option)) };
      }

      String val = values[values.length - 1];

      for (Resource<?> r : new PathspecParser(resourceFactory, shell.getCurrentResource(), val + "*")
               .resolve())
      {
         // Add result to the results list, and append a '/' if the
         // resource has children.
         String name = ("~".equals(val) ? "~/" : "") + r.getName()
                  + (r.isFlagSet(ResourceFlag.Node) ? "/" : "");
         results.add(name);
      }

      int lastNest = val.lastIndexOf(File.separatorChar);

      // Record the current index point in the buffer. If we're at
      // the separator char
      // set the value ahead by 1.
      state.setIndex((state.getOriginalIndex() - val.length()) + (lastNest != -1 ? lastNest + 1 : 0));
      state.getCandidates().addAll(results);
   }

   private boolean isFileResourceAssignable(final OptionMetadata option)
   {
      return Resource[].class.isAssignableFrom(option.getBoxedType())
               || Resource.class.isAssignableFrom(option.getBoxedType());
   }

}
