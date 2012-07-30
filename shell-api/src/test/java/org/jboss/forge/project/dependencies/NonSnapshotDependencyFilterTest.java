package org.jboss.forge.project.dependencies;

import org.junit.Assert;
import org.junit.Test;

public class NonSnapshotDependencyFilterTest
{
   @Test
   public void testNonSnapshotDependencyFilterWithSnapshot()
   {
      Dependency dep = DependencyBuilder.create().setGroupId("org.jboss.forge.test").setArtifactId("dependency-test")
               .setVersion("1.0.0-SNAPSHOT");
      Assert.assertFalse(new NonSnapshotDependencyFilter().accept(dep));
   }

   @Test
   public void testNonSnapshotDependencyFilterWithFinal()
   {
      Dependency dep = DependencyBuilder.create().setGroupId("org.jboss.forge.test").setArtifactId("dependency-test")
               .setVersion("1.0.0-Final");
      Assert.assertTrue(new NonSnapshotDependencyFilter().accept(dep));
   }
}
