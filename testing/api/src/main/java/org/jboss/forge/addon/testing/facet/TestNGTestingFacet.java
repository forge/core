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
 * Describes the TestNG testing framework.
 *
 * @author Ivan St. Ivanov
 */
@FacetConstraint(DependencyFacet.class)
public class TestNGTestingFacet extends AbstractTestingFacet implements TestingFacet
{

   public static final String TEST_NG_FRAMEWORK_NAME = "TestNG";
   public static final String TEST_NG_GROUP_ID = "org.testng";
   public static final String TEST_NG_ARTIFACT_ID = "testng";
   public static final String TEST_NG_SCOPE = "test";

   @Override
   public String getFrameworkName()
   {
      return TEST_NG_FRAMEWORK_NAME;
   }

   @Override
   protected DependencyBuilder buildFrameworkDependency()
   {
      return DependencyBuilder.create()
               .setGroupId(TEST_NG_GROUP_ID)
               .setArtifactId(TEST_NG_ARTIFACT_ID)
               .setScopeType(TEST_NG_SCOPE);
   }
}
