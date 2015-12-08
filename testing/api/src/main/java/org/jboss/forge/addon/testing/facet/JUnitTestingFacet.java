package org.jboss.forge.addon.testing.facet;

import org.jboss.forge.addon.dependencies.builder.DependencyBuilder;

/**
 * Describes the JUnit testing framework.
 *
 * @author Ivan St. Ivanov
 */
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
