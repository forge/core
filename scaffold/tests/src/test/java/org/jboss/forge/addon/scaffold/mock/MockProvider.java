package org.jboss.forge.addon.scaffold.mock;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.resource.ResourceFactory;
import org.jboss.forge.addon.scaffold.spi.AccessStrategy;
import org.jboss.forge.addon.scaffold.spi.ScaffoldGenerationContext;
import org.jboss.forge.addon.scaffold.spi.ScaffoldProvider;
import org.jboss.forge.addon.scaffold.spi.ScaffoldSetupContext;
import org.jboss.forge.addon.ui.command.UICommand;
import org.jboss.forge.addon.ui.context.UIContext;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class MockProvider implements ScaffoldProvider
{

   public static final String PROVIDER_NAME = "Mock Scaffold Provider";
   public static final String PROVIDER_DESCRIPTION = "Mock Scaffold Provider for use in tests";

   private boolean isSetup;

   @Inject
   private ResourceFactory resourceFactory;

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
   public List<Resource<?>> setup(Project project, ScaffoldSetupContext setupContext)
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
   public List<Resource<?>> generateFrom(Project project, ScaffoldGenerationContext generationContext)
   {
      List<Resource<?>> result = new ArrayList<Resource<?>>();
      for (Resource<?> resource : generationContext.getResources())
      {
         Scaffoldable scaffoldable = ((ScaffoldableResource) resource).getUnderlyingResourceObject();
         Scaffolded scaffolded = new Scaffolded(scaffoldable.getName());
         result.add(new ScaffoldedResource(resourceFactory, scaffolded));
      }
      return result;
   }

   @Override
   public List<Class<? extends UICommand>> getSetupFlow(Project project)
   {
      return null;
   }

   @Override
   public List<Class<? extends UICommand>> getGenerationFlow(Project project)
   {
      return null;
   }

   @Override
   public AccessStrategy getAccessStrategy()
   {
      return null;
   }
}
