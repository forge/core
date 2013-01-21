/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.spec.jpa;

import java.io.FileNotFoundException;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.shell.exceptions.PluginExecutionException;
import org.jboss.forge.shell.util.ConstraintInspector;
import org.jboss.forge.shell.util.Packages;
import org.jboss.forge.spec.javaee.PersistenceFacet;
import org.jboss.forge.spec.javaee.jpa.FieldPlugin;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class NewFieldPluginNegativeTest extends AbstractJPATest
{
   @Test(expected = FileNotFoundException.class)
   public void testNewFieldWithoutEntityDoesNotCreateFile() throws Exception
   {
      Project project = getProject();
      String entityName = "Goofy";

      queueInputLines(entityName);

      try {
         getShell().execute(ConstraintInspector.getName(FieldPlugin.class) + " int --named gamesPlayed");
         Assert.fail();
      }
      catch (PluginExecutionException e) {
         Assert.assertTrue(FieldPlugin.class.isAssignableFrom(e.getPlugin().getType()));
      }

      String pkg = project.getFacet(PersistenceFacet.class).getEntityPackage() + "." + entityName;
      String path = Packages.toFileSyntax(pkg) + ".java";

      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);

      java.getJavaResource(path).getJavaSource(); // exception here or die
   }
}
