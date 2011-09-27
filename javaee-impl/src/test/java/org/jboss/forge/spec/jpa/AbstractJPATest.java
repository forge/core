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
package org.jboss.forge.spec.jpa;

import static org.junit.Assert.assertFalse;

import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.util.ConstraintInspector;
import org.jboss.forge.shell.util.Packages;
import org.jboss.forge.spec.javaee.PersistenceFacet;
import org.jboss.forge.spec.javaee.jpa.EntityPlugin;
import org.jboss.forge.test.SingletonAbstractShellTest;
import org.junit.Before;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
public abstract class AbstractJPATest extends SingletonAbstractShellTest
{
   private int count = 0;

   @Before
   @Override
   public void beforeTest() throws Exception
   {
      super.beforeTest();
      initializeJavaProject();
      if ((getProject() != null) && !getProject().hasFacet(PersistenceFacet.class))
      {
         getShell().execute("project install-facet forge.spec.jpa");
      }
   }

   protected JavaClass generateEntity(final Project project) throws Exception
   {
      return generateEntity(project, null);
   }

   protected JavaClass generateEntity(final Project project, final String pkg) throws Exception
   {
      return generateEntity(project, pkg, "Goofy" + count++);
   }

   protected JavaClass generateEntity(final Project project, final String pkg, final String name) throws Exception
   {
      queueInputLines("");
      final StringBuilder commandBuilder = new StringBuilder(ConstraintInspector.getName(EntityPlugin.class))
               .append(" --named ").append(name);

      if (pkg != null)
      {
         commandBuilder.append(" --package ").append(pkg);
      }
      final Shell shell = getShell();
      shell.execute(commandBuilder.toString());

      JavaClass javaClass;
      if (shell.getCurrentResource() instanceof JavaResource)
      {
         javaClass = (JavaClass) ((JavaResource) shell.getCurrentResource()).getJavaSource();
      }
      else
      {
         final String entityClass = project.getFacet(PersistenceFacet.class).getEntityPackage() + "." + name;
         final String path = Packages.toFileSyntax(entityClass) + ".java";
         javaClass = (JavaClass) project.getFacet(JavaSourceFacet.class).getJavaResource(path).getJavaSource();
      }

      assertFalse(javaClass.hasSyntaxErrors());
      return javaClass;
   }
}
