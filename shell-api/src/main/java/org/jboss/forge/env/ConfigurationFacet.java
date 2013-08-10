package org.jboss.forge.env;

import javax.inject.Inject;

import org.jboss.forge.project.facets.BaseFacet;
import org.jboss.forge.shell.plugins.Alias;

/**
 * This facet allows easy access to the project-scoped {@link Configuration} object
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Alias("forge.configuration.facet")
public class ConfigurationFacet extends BaseFacet
{
   private ConfigurationFactory factory;

   private Configuration projectConfiguration;

   @Inject
   public ConfigurationFacet(ConfigurationFactory configurationFactory)
   {
      this.factory = configurationFactory;
   }

   public Configuration getConfiguration()
   {
      return projectConfiguration;
   }

   @Override
   public boolean install()
   {
      if (projectConfiguration == null)
      {
         projectConfiguration = factory.getProjectConfig(getProject());
      }
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return projectConfiguration != null;
   }
}
