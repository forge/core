package org.jboss.forge.addon;

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
   @Override
   public String getName()
   {
      return "Mock Scaffold Provider";
   }

   @Override
   public String getDescription()
   {
      return "Mock Scaffold Provider for use in tests";
   }

   @Override
   public List<Resource<?>> setup(Project project, ScaffoldSetupContext scaffoldContext)
   {
      return null;
   }

   @Override
   public List<Resource<?>> generateFrom(Project project, ScaffoldGenerationContext scaffoldContext)
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
