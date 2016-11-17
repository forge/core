/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.testing.facet;

import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;
import org.jboss.forge.addon.facets.constraints.FacetConstraint;
import org.jboss.forge.addon.projects.facets.DependencyFacet;

/**
 * Describes the JUnit testing framework.
 *
 * @author Ivan St. Ivanov
 */
@FacetConstraint(DependencyFacet.class)
public class JUnitTestingFacet extends AbstractTestingFacet implements TestingFacet
{

   public static final String JUNIT_FRAMEWORK_NAME = "JUnit";
   public static final String JUNIT_GROUP_ID = "junit";
   public static final String JUNIT_ARTIFACT_ID = "junit";
   public static final String JUNIT_SCOPE = "test";

   @Override
   public String getFrameworkName()
   {
      return JUNIT_FRAMEWORK_NAME;
   }

   @Override
   protected DependencyBuilder buildFrameworkDependency()
   {
      return DependencyBuilder.create()
               .setGroupId(JUNIT_GROUP_ID)
               .setArtifactId(JUNIT_ARTIFACT_ID)
               .setScopeType(JUNIT_SCOPE);
   }
}
