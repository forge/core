/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.maven.projects;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jboss.forge.addon.facets.FacetFactory;
import org.jboss.forge.addon.maven.projects.facets.MavenDependencyFacet;
import org.jboss.forge.addon.maven.projects.facets.MavenEnterpriseResourcesFacet;
import org.jboss.forge.addon.maven.projects.facets.MavenJavaCompilerFacet;
import org.jboss.forge.addon.maven.projects.facets.MavenJavaSourceFacet;
import org.jboss.forge.addon.maven.projects.facets.MavenMetadataFacet;
import org.jboss.forge.addon.maven.projects.facets.MavenPackagingFacet;
import org.jboss.forge.addon.maven.projects.facets.MavenResourcesFacet;
import org.jboss.forge.addon.maven.projects.facets.MavenWebResourcesFacet;
import org.jboss.forge.addon.parser.java.facets.JavaCompilerFacet;
import org.jboss.forge.addon.parser.java.facets.JavaSourceFacet;
import org.jboss.forge.addon.projects.AbstractProjectProvider;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFacet;
import org.jboss.forge.addon.projects.ProvidedProjectFacet;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.addon.projects.facets.EnterpriseResourcesFacet;
import org.jboss.forge.addon.projects.facets.MetadataFacet;
import org.jboss.forge.addon.projects.facets.PackagingFacet;
import org.jboss.forge.addon.projects.facets.ResourcesFacet;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;

/**
 * Implementation of the {@link MavenBuildSystem} interface
 * 
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
public class MavenBuildSystemImpl extends AbstractProjectProvider implements MavenBuildSystem
{
   private static final Logger log = Logger.getLogger(MavenBuildSystemImpl.class.getName());
   private Map<Class<? extends ProjectFacet>, Class<? extends ProjectFacet>> facets = new IdentityHashMap<>();

   @Override
   public String getType()
   {
      return "Maven";
   }

   @Override
   public Project createProject(final Resource<?> target)
   {
      Project project = new MavenProject(target);
      FacetFactory factory = SimpleContainer.getServices(getClass().getClassLoader(), FacetFactory.class).get();
      try
      {
         factory.install(project, MavenFacetImpl.class);
         factory.install(project, MavenPluginFacet.class);
         factory.install(project, MavenMetadataFacet.class);
         factory.install(project, MavenPackagingFacet.class);
         factory.install(project, MavenDependencyFacet.class);
         try
         {
            factory.register(project, MavenWebResourcesFacet.class);
         }
         catch (IllegalStateException e)
         {
            log.log(Level.FINE, "Could not install [" + MavenWebResourcesFacet.class.getName() + "] into project ["
                     + project + "]", e);
         }
      }
      catch (RuntimeException e)
      {
         throw new IllegalStateException("Could not install Maven into Project located at ["
                  + target.getFullyQualifiedName() + "]", e);
      }

      return project;
   }

   @Override
   public boolean containsProject(final Resource<?> target)
   {
      if (target.exists())
      {
         Resource<?> pom = target.getChild("pom.xml");
         return pom != null && pom.exists();
      }
      return false;
   }

   @Override
   public Set<Class<? extends ProvidedProjectFacet>> getProvidedFacetTypes()
   {
      Set<Class<? extends ProvidedProjectFacet>> result = new HashSet<>();
      result.add(MavenFacet.class);
      result.add(MavenPluginFacet.class);
      result.add(DependencyFacet.class);
      result.add(MetadataFacet.class);
      result.add(PackagingFacet.class);

      return Collections.unmodifiableSet(result);
   }

   @Override
   public int priority()
   {
      return 0;
   }

   @Override
   public Class<? extends ProjectFacet> resolveProjectFacet(Class<? extends ProjectFacet> facet)
   {
      if (facets.isEmpty())
      {
         facets.put(DependencyFacet.class, MavenDependencyFacet.class);
         facets.put(JavaCompilerFacet.class, MavenJavaCompilerFacet.class);
         facets.put(JavaSourceFacet.class, MavenJavaSourceFacet.class);
         facets.put(MetadataFacet.class, MavenMetadataFacet.class);
         facets.put(PackagingFacet.class, MavenPackagingFacet.class);
         facets.put(ResourcesFacet.class, MavenResourcesFacet.class);
         facets.put(WebResourcesFacet.class, MavenWebResourcesFacet.class);
         facets.put(EnterpriseResourcesFacet.class, MavenEnterpriseResourcesFacet.class);
      }
      return facets.getOrDefault(facet, facet);
   }

   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((getType() == null) ? 0 : getType().hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (!(obj instanceof MavenBuildSystemImpl))
         return false;
      MavenBuildSystemImpl other = (MavenBuildSystemImpl) obj;
      if (getType() == null)
      {
         if (other.getType() != null)
            return false;
      }
      else if (!getType().equals(other.getType()))
         return false;
      return true;
   }

}
