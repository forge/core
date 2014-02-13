package org.jboss.forge.addon.scaffold;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.scaffold.spi.AccessStrategy;
import org.jboss.forge.addon.scaffold.spi.ScaffoldGenerationContext;
import org.jboss.forge.addon.scaffold.spi.ScaffoldProvider;
import org.jboss.forge.addon.scaffold.spi.ScaffoldSetupContext;
import org.jboss.forge.addon.ui.command.UICommand;

import java.util.List;

public class MockScaffoldProvider implements ScaffoldProvider
{

   public static final String PROVIDER_NAME = "Mock Scaffold Provider";
   public static final String PROVIDER_DESCRIPTION = "Mock Scaffold Provider for use in tests";

   private boolean isSetup;

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
      return null;
   }

   @Override
   public List<Class<? extends UICommand>> getSetupFlow()
   {
      return null;
   }

   @Override
   public List<Class<? extends UICommand>> getGenerationFlow()
   {
      return null;
   }

   @Override
   public AccessStrategy getAccessStrategy()
   {
      return null;
   }
}
