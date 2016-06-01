/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.scaffold.mock;

import java.util.ArrayList;
import java.util.List;

import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.scaffold.spi.AccessStrategy;
import org.jboss.forge.addon.scaffold.spi.ScaffoldGenerationContext;
import org.jboss.forge.addon.scaffold.spi.ScaffoldProvider;
import org.jboss.forge.addon.scaffold.spi.ScaffoldSetupContext;
import org.jboss.forge.addon.ui.result.NavigationResult;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

public class MockProvider implements ScaffoldProvider
{

   public static final String PROVIDER_NAME = "Mock Scaffold Provider";
   public static final String PROVIDER_DESCRIPTION = "Mock Scaffold Provider for use in tests";

   private static boolean isSetup;
   private static boolean isGenerated;

   @Override
   public String getName()
   {
      return PROVIDER_NAME;
   }

   @Override
   public String getDescription()
   {
      return PROVIDER_DESCRIPTION;
   }

   @Override
   public List<Resource<?>> setup(ScaffoldSetupContext setupContext)
   {
      isSetup = true;
      return null;
   }

   @Override
   public boolean isSetup(ScaffoldSetupContext setupContext)
   {
      return isSetup;
   }

   @Override
   public List<Resource<?>> generateFrom(ScaffoldGenerationContext generationContext)
   {
      isGenerated = false;
      List<Resource<?>> result = new ArrayList<Resource<?>>();
      ResourceFactory resourceFactory = SimpleContainer.getServices(getClass().getClassLoader(), ResourceFactory.class)
               .get();
      for (Resource<?> resource : generationContext.getResources())
      {
         Scaffoldable scaffoldable = ((ScaffoldableResource) resource).getUnderlyingResourceObject();
         Scaffolded scaffolded = new Scaffolded(scaffoldable.getName());
         result.add(new ScaffoldedResource(resourceFactory, scaffolded));
      }
      isGenerated = true;
      return result;
   }

   @Override
   public NavigationResult getSetupFlow(ScaffoldSetupContext setupContext)
   {
      return null;
   }

   @Override
   public NavigationResult getGenerationFlow(ScaffoldGenerationContext generationContext)
   {
      return null;
   }

   @Override
   public AccessStrategy getAccessStrategy()
   {
      return null;
   }

   public boolean isGenerated()
   {
      return isGenerated;
   }
}
