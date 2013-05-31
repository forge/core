/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.command;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.enterprise.inject.spi.ProcessManagedBean;

import org.jboss.forge.bus.util.Annotations;
import org.jboss.forge.project.Facet;
import org.jboss.forge.project.Project;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.shell.plugins.Alias;
import org.jboss.forge.shell.plugins.Command;
import org.jboss.forge.shell.plugins.DefaultCommand;
import org.jboss.forge.shell.plugins.Help;
import org.jboss.forge.shell.plugins.Option;
import org.jboss.forge.shell.plugins.PipeIn;
import org.jboss.forge.shell.plugins.PipeOut;
import org.jboss.forge.shell.plugins.Plugin;
import org.jboss.forge.shell.plugins.RequiresResource;
import org.jboss.forge.shell.plugins.SetupCommand;
import org.jboss.forge.shell.plugins.Topic;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public class CommandLibraryExtension implements Extension
{
   private final Map<String, List<PluginMetadata>> plugins = new HashMap<String, List<PluginMetadata>>();
   private Set<Class<? extends Facet>> facetTypes = new HashSet<Class<? extends Facet>>();

   public Map<String, List<PluginMetadata>> getPlugins()
   {
      return plugins;
   }

   @SuppressWarnings("unchecked")
   public void scan(@Observes final ProcessManagedBean<?> event) throws Exception
   {
      Bean<?> bean = event.getBean();

      Class<?> clazz = bean.getBeanClass();

      if (Plugin.class.isAssignableFrom(clazz))
      {
         PluginMetadata pluginMeta = getMetadataFor((Class<? extends Plugin>) clazz);

         if (!plugins.containsKey(pluginMeta.getName()))
         {
            plugins.put(pluginMeta.getName(), new ArrayList<PluginMetadata>());
         }

         plugins.get(pluginMeta.getName()).add(pluginMeta);
      }

      if (Facet.class.isAssignableFrom(clazz) && !clazz.isInterface() && !clazz.isAnnotation())
      {
         facetTypes.add((Class<Facet>) clazz);
      }
   }

   /**
    * Facets should never inject Project. See FORGE-929
    */
   public <T extends Facet> void validateFacet(@Observes ProcessInjectionTarget<T> injectionTarget)
   {
      Class<T> facetClass = injectionTarget.getAnnotatedType().getJavaClass();
      Set<InjectionPoint> injectionPoints = injectionTarget.getInjectionTarget().getInjectionPoints();
      for (InjectionPoint injectionPoint : injectionPoints)
      {
         Type baseType = injectionPoint.getAnnotated().getBaseType();
         if (baseType instanceof Class && Project.class.isAssignableFrom((Class<?>) baseType))
         {
            throw new IllegalStateException("Facet "
                     + facetClass.getName()
                     + " must not @Inject Project. Please remove it and use getProject() instead.");
         }
      }
   }

   public Set<Class<? extends Facet>> getFacetTypes()
   {
      return Collections.unmodifiableSet(facetTypes);
   }

   public PluginMetadata getMetadataFor(final Class<? extends Plugin> plugin)
   {
      String name = getPluginName(plugin);

      PluginMetadataImpl pluginMeta = new PluginMetadataImpl();
      pluginMeta.setName(name);
      pluginMeta.setType(plugin);

      if (Annotations.isAnnotationPresent(plugin, Help.class))
      {
         pluginMeta.setHelp(Annotations.getAnnotation(plugin, Help.class).value());
      }
      else
      {
         pluginMeta.setHelp("");
      }

      if (Annotations.isAnnotationPresent(plugin, RequiresResource.class))
      {
         List<Class<? extends Resource<?>>> resourceTypes = Arrays.asList(Annotations.getAnnotation(plugin,
                  RequiresResource.class).value());

         pluginMeta.setResourceScopes(resourceTypes);
      }

      if (Annotations.isAnnotationPresent(plugin, Topic.class))
      {
         pluginMeta.setTopic(Annotations.getAnnotation(plugin, Topic.class).value());
      }

      processPluginCommands(pluginMeta, plugin);

      return pluginMeta;
   }

   @SuppressWarnings("rawtypes")
   private List<CommandMetadata> processPluginCommands(final PluginMetadataImpl pluginMeta, final Class<?> plugin)
   {
      List<CommandMetadata> results = new ArrayList<CommandMetadata>();

      for (Method method : plugin.getMethods())
      {
         if (Annotations.isAnnotationPresent(method, Command.class))
         {
            Command command = Annotations.getAnnotation(method, Command.class);
            CommandMetadataImpl commandMeta = new CommandMetadataImpl();
            commandMeta.setMethod(method);
            commandMeta.setHelp(command.help());
            commandMeta.setParent(pluginMeta);

            // Default commands are invoked via the name of the plug-in, not by
            // plug-in + command
            if ("".equals(command.value()))
            {
               commandMeta.setName(method.getName().trim().toLowerCase());
            }
            else
            {
               commandMeta.setName(command.value());
            }

            // This works because @DefaultCommand is annotated by @Command
            if (Annotations.isAnnotationPresent(method, DefaultCommand.class))
            {
               if (pluginMeta.hasDefaultCommand())
               {
                  throw new IllegalStateException("Plugins may only have one @"
                           + DefaultCommand.class.getSimpleName()
                           + ", but [" + pluginMeta.getType() + "] has more than one.");
               }

               commandMeta.setDefault(true);
               commandMeta.setName(pluginMeta.getName());

               // favor help text from this annotation over others
               DefaultCommand def = Annotations.getAnnotation(method, DefaultCommand.class);
               if ((def.help() != null) && !def.help().trim().isEmpty())
               {
                  commandMeta.setHelp(def.help());
               }
            }

            // This works because @SetupCommand is annotated by @Command
            if (Annotations.isAnnotationPresent(method, SetupCommand.class))
            {
               if (pluginMeta.hasSetupCommand())
               {
                  throw new IllegalStateException("Plugins may only have one @"
                           + SetupCommand.class.getSimpleName()
                           + ", but [" + pluginMeta.getType() + "] has more than one.");
               }

               commandMeta.setSetup(true);
               commandMeta.setName("setup");

               // favor help text from this annotation over others
               SetupCommand def = Annotations.getAnnotation(method, SetupCommand.class);
               if ((def.help() != null) && !def.help().trim().isEmpty())
               {
                  commandMeta.setHelp(def.help());
               }
            }

            /*
             * We don't want to do this if it is a setup command.
             */
            else if (Annotations.isAnnotationPresent(method, RequiresResource.class))
            {
               List<Class<? extends Resource>> resourceTypes = new ArrayList<Class<? extends Resource>>(
                        pluginMeta.getResourceScopes());

               resourceTypes.addAll(Arrays.asList(Annotations.getAnnotation(method, RequiresResource.class).value()));

               commandMeta.setResourceScopes(resourceTypes);
            }

            // fall back to the pluginMetadata for help text
            if ((commandMeta.getHelp() == null) || commandMeta.getHelp().trim().isEmpty())
            {
               commandMeta.setHelp(pluginMeta.getHelp());
            }

            Class<?>[] parameterTypes = method.getParameterTypes();
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();

            int i = 0;
            for (Class<?> clazz : parameterTypes)
            {
               OptionMetadataImpl optionMeta = new OptionMetadataImpl();

               optionMeta.setType(clazz);
               optionMeta.setIndex(i);

               if (PipeOut.class.isAssignableFrom(clazz))
               {
                  optionMeta.setPipeOut(true);
               }

               for (Annotation annotation : parameterAnnotations[i])
               {
                  if (annotation instanceof Option)
                  {
                     Option option = (Option) annotation;
                     optionMeta.setParent(commandMeta);
                     optionMeta.setName(option.name());
                     optionMeta.setShortName(option.shortName());
                     optionMeta.setFlagOnly(option.flagOnly());
                     optionMeta.setDescription(option.description());
                     optionMeta.setDefaultValue(option.defaultValue());
                     optionMeta.setHelp(option.help());
                     optionMeta.setRequired(option.required());
                     optionMeta.setPromptType(option.type());
                     optionMeta.setCompleterType(option.completer());

                  }
                  else if (annotation instanceof PipeIn)
                  {
                     optionMeta.setPipeIn(true);
                  }

               }
               commandMeta.addOption(optionMeta);

               i++;
            }

            results.add(commandMeta);
         }
      }

      pluginMeta.addCommands(results);

      return results;
   }

   private String getPluginName(final Class<?> plugin)
   {
      String name = null;

      if (Annotations.isAnnotationPresent(plugin, Alias.class))
      {
         Alias named = Annotations.getAnnotation(plugin, Alias.class);
         if (named != null)
         {
            name = named.value();
         }
      }

      if ((name == null) || "".equals(name.trim()))
      {
         name = plugin.getSimpleName();
      }
      return name.toLowerCase();
   }
}
