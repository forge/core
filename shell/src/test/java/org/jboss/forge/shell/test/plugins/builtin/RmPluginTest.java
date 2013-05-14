/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.plugins.builtin;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.matchers.JUnitMatchers.containsString;

import java.util.List;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.parser.JavaParser;
import org.jboss.forge.parser.java.JavaClass;
import org.jboss.forge.project.Project;
import org.jboss.forge.project.facets.JavaSourceFacet;
import org.jboss.forge.resources.Resource;
import org.jboss.forge.resources.java.JavaResource;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class RmPluginTest extends AbstractShellTest
{
   @Test
   public void testCanRmRF() throws Exception
   {
      Project project = initializeJavaProject();
      Shell shell = getShell();

      assertFalse(project.getProjectRoot().getChild("foo").exists());

      shell.execute("mkdir foo");
      assertTrue(project.getProjectRoot().getChild("foo").exists());

      shell.execute("rm -rf foo");
      assertFalse(project.getProjectRoot().getChild("foo").exists());

      shell.execute("mkdir f\\ o\\ o");
      assertTrue(project.getProjectRoot().getChild("f o o").exists());

      shell.execute("rm -rf f\\ o\\ o");
      assertFalse(project.getProjectRoot().getChild("f o o").exists());
   }

   @Test
   public void testRmField() throws Exception
   {
      Project project = initializeJavaProject();
      Shell shell = getShell();
      JavaSourceFacet java = project.getFacet(JavaSourceFacet.class);
      JavaClass javaClass = JavaParser
               .parse(JavaClass.class,
                        "public class MyClass { private String aField; public String getAField() {return aField;} public void setAField(String field){this.aField = field;}}");
      javaClass.setPackage("org.example");
      JavaResource javaResource = java.saveJavaSource(javaClass);
      shell.setCurrentResource(javaResource);

      // Execute SUT
      queueInputLines("y", "y", "y");
      shell.execute("rm aField");

      // Check results
      List<?> members = javaResource.getJavaSource().getMembers();
      assertTrue(members.isEmpty());
   }

   @Test
   public void testErrorRemovingMissingResource() throws Exception
   {
      final Resource<?> missingResource = getProject().getProjectRoot().getChild("foo-file.txt");
      getShell().execute("rm " + missingResource.getName());
      assertThat(getOutput(), containsString("rm: cannot remove '" + missingResource.getFullyQualifiedName()
               + "': No such resource exists"));

   }
}
