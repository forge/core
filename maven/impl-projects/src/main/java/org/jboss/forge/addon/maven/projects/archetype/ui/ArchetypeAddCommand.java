/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects.archetype.ui;

import org.jboss.forge.addon.resource.URLResource;
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
public class ArchetypeAddCommand extends AbstractArchetypeCommand
{

   private UIInput<String> named;
   private UIInput<URLResource> url;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      InputComponentFactory factory = builder.getInputComponentFactory();
      named = factory.createInput("named", String.class).setLabel("Archetype catalog Name")
               .setDescription("The archetype catalog name to be used").setRequired(true);
      url = factory.createInput("url", URLResource.class).setLabel("Archetype catalog URL")
               .setDescription("The archetype catalog URL to be used").setRequired(true);
      builder.add(named).add(url);
   }

   @Override
   public UICommandMetadata getMetadata(UIContext context)
   {
      return Metadata.forCommand(getClass()).name("Archetype: Add").category(Categories.create("Maven"))
               .description("Adds an archetype catalog to the Forge configuration file");
   }

   @Override
   public Result execute(UIExecutionContext context) throws Exception
   {
      getConfiguration().setProperty(named.getValue(), url.getValue().getFullyQualifiedName());
      getArchetypeCatalogFactoryRegistry().addArchetypeCatalogFactory(named.getValue(),
               url.getValue().getUnderlyingResourceObject());
      return Results.success();
   }

}
