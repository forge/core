/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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

package org.jboss.forge.maven.facets;

import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.dependencies.DependencyBuilder;
import org.jboss.forge.project.dependencies.DependencyInstaller;
import org.jboss.forge.project.dependencies.ScopeType;
import org.jboss.forge.project.facets.DependencyFacet;
import org.jboss.forge.project.packaging.PackagingType;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class MavenDependencyFacetDependencyInstallerTest extends AbstractShellTest
{
   @Inject
   private DependencyInstaller installer;

   @Test
   public void testInstall() throws Exception
   {
      Project project = initializeProject(PackagingType.BASIC);

      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      DependencyBuilder dependency = DependencyBuilder.create("org.jboss.forge:forge-shell-api");

      queueInputLines("13");
      installer.install(project, dependency);

      Assert.assertTrue(deps.hasDirectDependency(dependency));
      Assert.assertTrue(deps.hasDirectManagedDependency(dependency));
      Assert.assertEquals("1.0.4.Final", deps.getManagedDependency(dependency).getVersion());
      Assert.assertNull(deps.getDirectDependency(dependency).getVersion());
   }

   @Test
   public void testInstallWithVersion() throws Exception
   {
      Project project = initializeProject(PackagingType.BASIC);

      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      DependencyBuilder dependency = DependencyBuilder.create("org.jboss.forge:forge-shell-api:1.0.4.Final");

      installer.install(project, dependency);

      Assert.assertTrue(deps.hasDirectDependency(dependency));
      Assert.assertTrue(deps.hasDirectManagedDependency(dependency));
      Assert.assertEquals("1.0.4.Final", deps.getManagedDependency(dependency).getVersion());
      Assert.assertNull(deps.getDirectDependency(dependency).getVersion());
   }

   @Test
   public void testInstallManaged() throws Exception
   {
      Project project = initializeProject(PackagingType.BASIC);

      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      DependencyBuilder dependency = DependencyBuilder.create("org.jboss.forge:forge-shell-api");

      queueInputLines("13");
      installer.installManaged(project, dependency);

      Assert.assertFalse(deps.hasEffectiveDependency(dependency));
      Assert.assertTrue(deps.hasDirectManagedDependency(dependency));
      Assert.assertEquals("1.0.4.Final", deps.getManagedDependency(dependency).getVersion());
   }

   @Test
   public void testInstallManagedWithVersion() throws Exception
   {
      Project project = initializeProject(PackagingType.BASIC);

      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      DependencyBuilder dependency = DependencyBuilder.create("org.jboss.forge:forge-shell-api:1.0.4.Final");

      installer.installManaged(project, dependency);

      Assert.assertFalse(deps.hasEffectiveDependency(dependency));
      Assert.assertTrue(deps.hasDirectManagedDependency(dependency));
      Assert.assertEquals("1.0.4.Final", deps.getManagedDependency(dependency).getVersion());
   }



   @Test
   public void testInstallScoped() throws Exception
   {
      Project project = initializeProject(PackagingType.BASIC);

      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      DependencyBuilder dependency = DependencyBuilder.create("org.jboss.forge:forge-shell-api").setScopeType(ScopeType.TEST);

      queueInputLines("13");
      installer.install(project, dependency);

      Assert.assertTrue(deps.hasDirectDependency(dependency));
      Assert.assertTrue(deps.hasDirectManagedDependency(dependency));
      Assert.assertEquals("1.0.4.Final", deps.getManagedDependency(dependency).getVersion());
      Assert.assertNull(deps.getDirectDependency(dependency).getVersion());
      Assert.assertEquals(ScopeType.TEST, deps.getManagedDependency(dependency).getScopeTypeEnum());
      Assert.assertEquals(ScopeType.TEST, deps.getEffectiveDependency(dependency).getScopeTypeEnum());
   }

   @Test
   public void testInstallWithVersionScoped() throws Exception
   {
      Project project = initializeProject(PackagingType.BASIC);

      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      DependencyBuilder dependency = DependencyBuilder.create("org.jboss.forge:forge-shell-api:1.0.4.Final").setScopeType(ScopeType.TEST);

      installer.install(project, dependency);

      Assert.assertTrue(deps.hasDirectDependency(dependency));
      Assert.assertTrue(deps.hasDirectManagedDependency(dependency));
      Assert.assertEquals("1.0.4.Final", deps.getManagedDependency(dependency).getVersion());
      Assert.assertNull(deps.getDirectDependency(dependency).getVersion());
      Assert.assertEquals(ScopeType.TEST, deps.getManagedDependency(dependency).getScopeTypeEnum());
      Assert.assertEquals(ScopeType.TEST, deps.getEffectiveDependency(dependency).getScopeTypeEnum());
   }

   @Test
   public void testInstallManagedScoped() throws Exception
   {
      Project project = initializeProject(PackagingType.BASIC);

      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      DependencyBuilder dependency = DependencyBuilder.create("org.jboss.forge:forge-shell-api").setScopeType(ScopeType.TEST);

      queueInputLines("13");
      installer.installManaged(project, dependency);

      Assert.assertFalse(deps.hasEffectiveDependency(dependency));
      Assert.assertTrue(deps.hasDirectManagedDependency(dependency));
      Assert.assertEquals("1.0.4.Final", deps.getManagedDependency(dependency).getVersion());
      Assert.assertEquals(ScopeType.TEST, deps.getManagedDependency(dependency).getScopeTypeEnum());
   }

   @Test
   public void testInstallManagedWithVersionScoped() throws Exception
   {
      Project project = initializeProject(PackagingType.BASIC);

      DependencyFacet deps = project.getFacet(DependencyFacet.class);
      DependencyBuilder dependency = DependencyBuilder.create("org.jboss.forge:forge-shell-api:1.0.4.Final").setScopeType(ScopeType.TEST);

      installer.installManaged(project, dependency);

      Assert.assertFalse(deps.hasEffectiveDependency(dependency));
      Assert.assertTrue(deps.hasDirectManagedDependency(dependency));
      Assert.assertEquals("1.0.4.Final", deps.getManagedDependency(dependency).getVersion());
      Assert.assertEquals(ScopeType.TEST, deps.getManagedDependency(dependency).getScopeTypeEnum());
   }
}
