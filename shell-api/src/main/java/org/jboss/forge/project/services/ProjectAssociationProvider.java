package org.jboss.forge.project.services;

import org.jboss.forge.project.Project;
import org.jboss.forge.resources.DirectoryResource;

import java.util.List;

/**
 * @author <a href="mailto:torben@jit-central.com">Torben Jaeger</a>
 */
public interface ProjectAssociationProvider {
   void setProjectFactory(ProjectFactory factory);
   void associate(Project project, DirectoryResource parent);
}
