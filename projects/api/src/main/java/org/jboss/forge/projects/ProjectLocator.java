package org.jboss.forge.projects;

import org.jboss.forge.container.services.Exported;
import org.jboss.forge.resource.DirectoryResource;
import org.jboss.forge.resource.Resource;

/**
 * Locates project root directories, and creates instances of projects for that type.
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@Exported
public interface ProjectLocator
{
   /**
    * Create a {@link Project} of the given {@link ProjectType} in the specified {@link DirectoryResource}.
    */
   public Project createProject(DirectoryResource targetDir, ProjectType value);

   /**
    * Returns true if the given {@link Resource} contains an existing {@link Project}.
    */
   public boolean containsProject(Resource<?> resource);
}