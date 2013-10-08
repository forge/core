package org.jboss.forge.addon.projects;

import java.util.Set;

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.resource.DirectoryResource;

/**
 * Creates and locates {@link Project} instances for a specific build system. E.g: Maven, Gradle, and so on...
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public interface BuildSystem
{
   /**
    * Return the human-readable name for this {@link BuildSystem}. This should be relatively unique.
    */
   public String getType();

   /**
    * Return the {@link Set} of default {@link Facet} types provided by {@link Project} instances of this
    * {@link BuildSystem} type.
    */
   public Iterable<Class<? extends BuildSystemFacet>> getProvidedFacetTypes();

   /**
    * Create a new or existing {@link Project} with the given {@link DirectoryResource} as
    * {@link Project#getProjectRoot()}.
    */
   public Project createProject(DirectoryResource targetDir);

   /**
    * Returns true if the given {@link DirectoryResource} contains an existing {@link Project}.
    */
   public boolean containsProject(DirectoryResource resource);
}