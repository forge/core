/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
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
         queueInputLines("", "", "");
         getShell().execute("persistence setup --provider HIBERNATE --container JBOSS_AS7");
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
