/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.maven.facets;

import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.MetadataFacet;
import org.jboss.forge.project.facets.PackagingFacet;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Test;

public class MavenPackagingFacetTest extends AbstractShellTest
{
   @Test
   public void testHasFacet() throws Exception
   {
      Assert.assertTrue("PackagingFacet not installed in project", getProject().hasFacet(PackagingFacet.class));

   }

   @Test
   public void testFinalName() throws Exception
   {
      final Project project = getProject();
      final PackagingFacet facet = project.getFacet(PackagingFacet.class);
      Assert.assertNotNull("Final name is null", facet.getFinalName());
      MetadataFacet mFacet = project.getFacet(MetadataFacet.class);
      String finalName = mFacet.getProjectName() + "-" + mFacet.getProjectVersion();
      Assert.assertEquals(finalName, facet.getFinalName());
   }
}
