/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.archetype.ui;

import java.io.PrintStream;

import org.apache.maven.archetype.catalog.Archetype;
import org.apache.maven.archetype.catalog.ArchetypeCatalog;
import org.jboss.forge.addon.maven.archetype.ArchetypeCatalogFactory;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.output.UIOutput;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ArchetypeListCommand extends AbstractArchetypeCommand
{
   private UIInput<String> named;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      InputComponentFactory factory = builder.getInputComponentFactory();
      named = factory.createInput("named", String.class).setLabel("Archetype catalog Name")
               .setDescription("The archetype catalog name to be used");
      builder.add(named);
   }

   @Override
   public boolean isEnabled(UIContext context)
   {
      return !context.getProvider().isGUI();
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Archetype: List").category(Categories.create("Maven"))
               .description("Lists the registered archetype catalogs from the Forge configuration file");
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      UIOutput output = context.getUIContext().getProvider().getOutput();
      PrintStream out = output.out();
      if (named.hasValue())
      {
         ArchetypeCatalogFactory archetypeCatalogFactory = getArchetypeCatalogFactoryRegistry()
                  .getArchetypeCatalogFactory(named.getValue());
         if (archetypeCatalogFactory != null)
         {
            ArchetypeCatalog archetypeCatalog = archetypeCatalogFactory.getArchetypeCatalog();
            if (archetypeCatalog != null)
            {
               for (Archetype archetype : archetypeCatalog.getArchetypes())
               {
                  out.println(archetype);
               }
            }
         }
      }
      else
      {
         for (ArchetypeCatalogFactory factory : getArchetypeCatalogFactoryRegistry().getArchetypeCatalogFactories())
         {
            String key = factory.getName();
            String catalog = factory.toString();
            out.println(key + " = " + catalog);
         }
      }
      return Results.success();
   }

}
