package org.jboss.forge.project.facets;

import javax.inject.Inject;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.env.ConfigurationScope;
import org.jboss.forge.shell.plugins.Alias;

/**
 * This facet allows easy access to the project-scoped {@link Configuration} object
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@Alias("forge.configuration.facet")
public class ConfigurationFacet extends BaseFacet
{
   @Inject
   private Configuration configuration;

   private Configuration projectConfiguration;

   public Configuration getConfiguration()
   {
      return projectConfiguration;
   }

   @Override
   public boolean install()
   {
      projectConfiguration = configuration.getScopedConfiguration(ConfigurationScope.PROJECT);
      return true;
   }

   @Override
   public boolean isInstalled()
   {
      return projectConfiguration != null;
   }
}
