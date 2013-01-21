/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.project;

import static org.junit.Assert.fail;

import org.jboss.forge.resources.DirectoryResource;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 * 
 */
public class BaseProjectTest
{
   Project project = new BaseProject()
   {
      @Override
      public DirectoryResource getProjectRoot()
      {
         return null;
      }

      @Override
      public boolean exists()
      {
         return false;
      }
   };

   @Test
   public void testFacetInstallationReturningFalseCancelsInstallation()
   {
      Assert.assertFalse(project.hasFacet(MockFacet.class));
      Assert.assertNull(project.getAttribute(MockFacet.INSTALLED));
      try
      {
         project.installFacet(new MockFacet(false));
         fail();
      }
      catch (Exception e)
      {
      }
      Assert.assertFalse(project.hasFacet(MockFacet.class));
   }

   @Test
   public void testCanInstallFacetWhichIsInstalledButNotRegistered()
   {
      Assert.assertFalse(project.hasFacet(MockFacet.class));
      Assert.assertNull(project.getAttribute(MockFacet.INSTALLED));
      project.installFacet(new MockFacet());
      Assert.assertTrue(project.hasFacet(MockFacet.class));
      Assert.assertNotNull(project.getAttribute(MockFacet.INSTALLED));
   }

   @Test
   public void testCanRegisterFacetWhichIsNotInstalledAndRequiresNoInstallation()
   {
      Assert.assertFalse(project.hasFacet(MockFacet.class));
      Assert.assertNull(project.getAttribute(MockFacet.INSTALLED));
      project.registerFacet(new MockFacet());
      Assert.assertTrue(project.hasFacet(MockFacet.class));
      Assert.assertTrue(project.getFacet(MockFacet.class).isInstalled());
   }

}
