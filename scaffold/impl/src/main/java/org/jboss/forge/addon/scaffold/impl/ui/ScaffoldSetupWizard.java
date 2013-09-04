/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.scaffold.impl.ui;

import javax.inject.Inject;

import org.jboss.forge.addon.resource.DirectoryResource;
import org.jboss.forge.addon.scaffold.spi.ScaffoldProvider;
import org.jboss.forge.addon.ui.AbstractUICommand;
import org.jboss.forge.addon.ui.context.UIBuilder;
import org.jboss.forge.addon.ui.context.UIContext;
import org.jboss.forge.addon.ui.input.UIInput;
import org.jboss.forge.addon.ui.input.UISelectOne;
import org.jboss.forge.addon.ui.metadata.WithAttributes;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.addon.ui.result.Result;
import org.jboss.forge.addon.ui.result.Results;
import org.jboss.forge.addon.ui.util.Categories;
import org.jboss.forge.addon.ui.util.Metadata;
import org.jboss.forge.addon.ui.wizard.UIWizard;

/**
 * Scaffold wizard
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class ScaffoldSetupWizard extends AbstractUICommand implements UIWizard
{
   @Inject
   @WithAttributes(label = "Provider", required = true)
   private UISelectOne<ScaffoldProvider> provider;

   @Inject
   @WithAttributes(label = "Target Directory", required = true)
   private UIInput<DirectoryResource> target;

   @Inject
   @WithAttributes(label = "Overwrite existing files?")
   private UIInput<Boolean> overwrite;

   @Inject
   @WithAttributes(label = "Install Templates in src/main/templates?")
   private UIInput<Boolean> installTemplates;

   @Override
   public void initializeUI(UIBuilder builder) throws Exception
   {
      builder.add(provider).add(target).add(overwrite).add(installTemplates);
   }

   @Override
   public Metadata getMetadata()
   {
      return super.getMetadata().name("scaffold").description("Setups the scaffold")
               .category(Categories.create("Scaffold", "Setup"));
   }

   @Override
   public Result execute(UIContext context) throws Exception
   {
      return Results.success();
   }

   @Override
   public NavigationResult next(UIContext context) throws Exception
   {
      return Results.navigateTo(provider.getValue().getSetupFlow());
   }
}
