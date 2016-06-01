/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.testing.facet;

import org.jboss.forge.addon.dependencies.Coordinate;
import org.jboss.forge.addon.dependencies.Dependency;
import org.jboss.forge.addon.dependencies.DependencyResolver;
import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.dependencies.builder.DependencyQueryBuilder;
import org.jboss.forge.addon.projects.facets.AbstractProjectFacet;
import org.jboss.forge.addon.projects.facets.DependencyFacet;
import org.jboss.forge.furnace.container.simple.lifecycle.SimpleContainer;
import org.jboss.forge.furnace.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provides operations that are common for all the testing frameworks supported by this addon.
 *
 * @author Ivan St. Ivanov
 */
public abstract class AbstractTestingFacet extends AbstractProjectFacet implements TestingFacet
{

   private String frameworkVersion;

   @Override
   public void setFrameworkVersion(String version)
   {
      this.frameworkVersion = version;
   }

   @Override
   public boolean install()
   {
      Assert.notNull(frameworkVersion, "You should pick testing framework version before installing this facet");
      final DependencyFacet dependencyFacet = getDependencyFacet();
      getMatchingDependencies(dependencyFacet.getDependencies())
               .forEach(dependencyFacet::removeDependency);
      dependencyFacet.addDirectDependency(buildFrameworkDependency().setVersion(frameworkVersion));
      return true;
   }

   private DependencyFacet getDependencyFacet()
   {
      return getFaceted().getFacet(DependencyFacet.class);
   }

   private Stream<Dependency> getMatchingDependencies(List<Dependency> existingDependencies)
   {
      final List<Dependency> frameworkCoordinates = getFrameworkDependencies();
      return existingDependencies.stream()
               .filter(existingDependency ->
                        frameworkCoordinates.stream()
                                 .anyMatch(
                                          frameworkDependency -> areCoordinatesMatching(
                                                   frameworkDependency.getCoordinate(),
                                                   existingDependency.getCoordinate())));
   }

   private boolean areCoordinatesMatching(Coordinate coordinate1, Coordinate coordinate2)
   {
      return coordinate1.getArtifactId().equals(coordinate2.getArtifactId()) &&
               coordinate1.getGroupId().equals(coordinate2.getGroupId());
   }

   @Override
   public boolean isInstalled()
   {
      return getMatchingDependencies(getDependencyFacet().getDependencies()).count() ==
               getFrameworkDependencies().size();
   }

   @Override
   public List<Dependency> getFrameworkDependencies()
   {
      return Collections.singletonList(buildFrameworkDependency());
   }

   @Override
   public List<String> getAvailableVersions()
   {
      final List<Coordinate> availableCoordinates = getDependencyResolver().resolveVersions(
               DependencyQueryBuilder.create(buildFrameworkDependency().getCoordinate()));
      return availableCoordinates.stream()
               .map(Coordinate::getVersion)
               .collect(Collectors.toList());
   }

   private DependencyResolver resolver;

   private DependencyResolver getDependencyResolver()
   {
      if (resolver == null)
         resolver = SimpleContainer.getServices(getClass().getClassLoader(), DependencyResolver.class).get();
      return resolver;
   }

   protected abstract DependencyBuilder buildFrameworkDependency();
}
