package org.jboss.forge.addon.projects;

import java.util.Set;

import org.jboss.forge.addon.facets.Facet;
import org.jboss.forge.addon.resource.Resource;

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
   String getType();

   /**
    * Return the {@link Set} of default {@link Facet} types provided by {@link Project} instances of this
    * {@link ProjectProvider} type.
    */
   Iterable<Class<? extends ProvidedProjectFacet>> getProvidedFacetTypes();

   /**
    * Create a new or existing {@link Project} with the given {@link Resource} as {@link Project#getRootDirectory()}.
    */
   Project createProject(Resource<?> target);

   /**
    * Returns true if the given {@link Resource} contains an existing {@link Project}.
    */
   boolean containsProject(Resource<?> resource);

   /**
    * Returns the priority of this {@link ProjectProvider}. Lower values receive a higher priority.
    */
   int priority();
}