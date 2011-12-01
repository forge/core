package org.jboss.forge.project.services;

import org.jboss.forge.project.Project;
import org.jboss.forge.resources.DirectoryResource;

/**
 * @author <a href="mailto:torben@jit-central.com">Torben Jaeger</a>
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ProjectAssociationProvider
{
   /**
    * Return true if this provide is capable of creating a parent-child association between the given Project and the
    * given parent directory.
    */
   boolean canAssociate(Project project, DirectoryResource parent);

   /**
    * Create a parent-child association between the given Project and the given parent directory.
    */
   void associate(Project project, DirectoryResource parent);
}
