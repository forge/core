package org.jboss.forge.env;

import org.jboss.forge.project.Project;

/**
 * Creates {@link Configuration} objects for the requested scopes
 * 
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public interface ConfigurationFactory
{
   public Configuration getUserConfig();

   public Configuration getProjectConfig(Project project);
}
