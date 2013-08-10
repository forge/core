package org.jboss.forge.shell.env;

import javax.enterprise.event.Event;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.forge.env.Configuration;
import org.jboss.forge.env.ConfigurationException;
import org.jboss.forge.env.ConfigurationFacet;
import org.jboss.forge.env.ConfigurationFactory;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.events.InstallFacets;
import org.jboss.forge.shell.Shell;

public class ConfigurationProducer
{

   @Inject
   private ConfigurationFactory factory;

   @Inject
   private Event<InstallFacets> installFacets;

   /**
    * Returns a project-scoped configuration (if a project is in scope). Falls back to the user scope configuration if
    * no project is set in the shell
    * 
    * @return
    * @throws ConfigurationException
    */
   @Produces
   public Configuration getConfiguration(Shell shell) throws ConfigurationException
   {
      Project project = shell.getCurrentProject();
      if (project == null)
      {
         return factory.getUserConfig();
      }
      if (!project.hasFacet(ConfigurationFacet.class))
      {
         installFacets.fire(new InstallFacets(ConfigurationFacet.class));
         // // Installing manually
         // ConfigurationFacet facet = new ConfigurationFacet(factory);
         // facet.setProject(project);
         // project.installFacet(facet);
      }
      return project.getFacet(ConfigurationFacet.class).getConfiguration();
   }
}
