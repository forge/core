/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.archetype.ui;

import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.context.UIExecutionContext;
import org.jboss.forge.addon.ui.input.InputComponentFactory;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.metadata.UICommandMetadata;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;

/**
 * 
 * @author <a href="mailto:ggastald@redhat.com">George Gastaldi</a>
 */
public class ArchetypeRemoveCommand extends AbstractArchetypeCommand
{
   private UIInput<String> named;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      InputComponentFactory factory = builder.getInputComponentFactory();
      named = factory.createInput("named", String.class).setLabel("Archetype catalog Name")
               .setDescription("The archetype catalog name to be used").setRequired(true);
      builder.add(named);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Archetype: Remove").category(Categories.create("Maven"))
               .description("Removes an archetype catalog from the Forge configuration file");
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      getConfiguration().clearProperty(named.getValue());
      getArchetypeCatalogFactoryRegistry().removeArchetypeCatalogFactory(named.getValue());
      return Results.success();
   }

}
