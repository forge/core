/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.devtools.java;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.parser.java.resources.JavaResource;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.projects.ui.AbstractProjectCommand;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.ui.InputComponentFactory;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.facets.HintsFacet;
import org.jboss.forge.addon.ui.hints.InputType;
import org.jboss.forge.addon.ui.input.InputComponent;
import org.jboss.forge.addon.ui.input.UICompleter;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.furnace.Furnace;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.parser.java.InterfaceCapable;
import org.jboss.forge.parser.java.JavaSource;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@FacetConstraint({ JavaSourceFacet.class, ResourcesFacet.class })
public class RegisterAsServiceCommand extends AbstractProjectCommand
{
   private final InputComponentFactory inputFactory;

   private UIInput<JavaResource> type;
   private UIInput<String> serviceType;

   private final ProjectFactory projectFactory;

   public RegisterAsServiceCommand()
   {
      Furnace furnace = SimpleContainer.getFurnace(this.getClass().getClassLoader());
      this.inputFactory = furnace.getAddonRegistry().getServices(InputComponentFactory.class).get();
      this.projectFactory = furnace.getAddonRegistry().getServices(ProjectFactory.class).get();
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(RegisterAsServiceCommand.class).category(Categories.create("Java", "ServiceLoader"))
               .description("Register a Java type as a service implementation.")
               .name("Register as ServiceLoader service");
   }

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      type = inputFactory.createInput("type", JavaResource.class);
      type.getFacet(HintsFacet.class).setInputType(InputType.JAVA_CLASS_PICKER);
      type.setRequired(true);

      Object selection = builder.getUIContext().getInitialSelection().get();
      if (selection instanceof JavaResource)
         type.setDefaultValue((JavaResource) selection);

      serviceType = inputFactory.createInput("serviceInterface", String.class);
      serviceType.getFacet(HintsFacet.class).setInputType(InputType.JAVA_CLASS_PICKER);
      serviceType.setRequired(true);
      serviceType.setCompleter(new UICompleter<String>()
      {
         @Override
         public Iterable<String> getCompletionProposals(UIContext context, InputComponent<?, String> input, String value)
         {
            Set<String> result = new LinkedHashSet<String>();
            if (type.getValue() != null)
            {
               try
               {
                  JavaSource<?> source = type.getValue().getJavaSource();
                  if (source instanceof InterfaceCapable)
                  {
                     result.addAll(((InterfaceCapable<?>) source).getInterfaces());
                  }
               }
               catch (FileNotFoundException e)
               {
               }
            }
            return result;
         }
      });

      builder.add(type).add(serviceType);

   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      String implName = type.getValue().reify(JavaResource.class).getJavaSource().getQualifiedName();
      String interfaceName = serviceType.getValue();

      FileResource<?> registryFile = getSelectedProject(context).getFacet(ResourcesFacet.class).getResource(
               "META-INF/services/" + interfaceName);
      if (!registryFile.exists())
      {
         registryFile.createNewFile();
         registryFile.setContents(implName);
      }
      else
      {
         StringBuilder builder = new StringBuilder();
         BufferedReader reader = new BufferedReader(new InputStreamReader(registryFile.getResourceInputStream()));

         boolean found = false;
         String line = null;
         while ((line = reader.readLine()) != null)
         {
            if (line.matches("\\s*" + interfaceName + "\\s*"))
            {
               found = true;
            }
            builder.append(line);
         }

         if (!found)
         {
            builder.append("\n" + implName);
            registryFile.setContents(builder.toString());
         }
      }

      return Results.success("Type '" + implName + "' is registered as service of type '" + interfaceName + "'");
   }

   @Override
   protected boolean isProjectRequired()
   {
      return true;
   }

   @Override
   protected ProjectFactory getProjectFactory()
   {
      return projectFactory;
   }

}
