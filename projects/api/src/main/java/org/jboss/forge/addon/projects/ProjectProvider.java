package org.jboss.forge.addon.projects;

import java.util.Set;

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.resource.DirectoryResource;

/**
 * Creates and locates {@link Project} instances for a specific technology. E.g: Maven, Gradle, JavaScript, HTML, and so
 * on...
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface ProjectProvider
{
   /**
    * Return the human-readable name for this {@link ProjectProvider}. This should be relatively unique.
    */
   public String getType();

   /**
    * Return the {@link Set} of default {@link Facet} types provided by {@link Project} instances of this
    * {@link ProjectProvider} type.
    */
   public Iterable<Class<? extends ProvidedProjectFacet>> getProvidedFacetTypes();

   /**
    * Create a new or existing {@link Project} with the given {@link DirectoryResource} as
    * {@link Project#getProjectRoot()}.
    */
   public Project createProject(DirectoryResource targetDir);

   /**
    * Returns true if the given {@link DirectoryResource} contains an existing {@link Project}.
    */
   public boolean containsProject(DirectoryResource resource);

   /**
    * Returns the priority of this {@link ProjectProvider}. Lower values receive a higher priority.
    */
   public int priority();
}