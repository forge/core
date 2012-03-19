/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.forge.project;

import static org.junit.Assert.*;

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
      {}
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
