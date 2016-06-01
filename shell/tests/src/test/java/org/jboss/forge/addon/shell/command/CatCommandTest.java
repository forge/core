/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.shell.command;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.ProjectFactory;
import org.jboss.forge.addon.resource.FileResource;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.addon.ui.result.Failed;
import org.jboss.forge.addon.ui.result.Result;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class CatCommandTest
{
   @Inject
   private ShellTest shellTest;

   @Inject
   private ProjectFactory projectFactory;

   @Before
   public void setUp() throws Exception
   {
      shellTest.clearScreen();
   }

   @After
   public void tearDown() throws Exception
   {
      shellTest.close();
   }

   @Test
   public void testCatCommandInvalidArgument() throws Exception
   {
      Result result = shellTest.execute("cat foo bar", 15, TimeUnit.SECONDS);
      Assert.assertThat(result, instanceOf(Failed.class));
      String err = shellTest.getStdErr();
      Assert.assertThat(err, containsString("cat: foo: No such file or directory"));
      Assert.assertThat(err, containsString("cat: bar: No such file or directory"));
   }

   @Test
   public void testCatCommand() throws Exception
   {
      Project project = projectFactory.createTempProject();
      File target = new File(project.getRoot().getFullyQualifiedName(), "test.java");
      target.createNewFile();

      FileResource<?> source = project.getRoot().getChild(target.getName()).reify(FileResource.class);
      source.setContents("public void test() {}");

      shellTest.execute("cat " + source.getFullyQualifiedName(), 15, TimeUnit.SECONDS);
      Assert.assertThat(shellTest.getStdOut(), containsString("test()"));
   }

   @Test
   public void testCatColoredCommand() throws Exception
   {
      Project project = projectFactory.createTempProject();
      File target = new File(project.getRoot().getFullyQualifiedName(), "test.java");
      target.createNewFile();

      FileResource<?> source = project.getRoot().getChild(target.getName()).reify(FileResource.class);
      source.setContents("public void test() {}");

      shellTest.execute("cat " + source.getFullyQualifiedName() + " --color", 15, TimeUnit.SECONDS);
      // the string should be colors, so there are color codes between the statements
      Assert.assertThat(shellTest.getStdOut(), not(containsString("public void")));
   }

   @Test
   // FORGE-2421
   public void testCatColoredCommandMissingType() throws Exception
   {
      Project project = projectFactory.createTempProject();
      File target = new File(project.getRoot().getFullyQualifiedName(), "test");
      target.createNewFile();

      FileResource<?> source = project.getRoot().getChild(target.getName()).reify(FileResource.class);
      source.setContents("public void test() {}");

      shellTest.execute("cat " + source.getFullyQualifiedName() + " --color", 15, TimeUnit.SECONDS);
      Assert.assertThat(shellTest.getStdErr(), containsString("Error while rendering output in color"));
      // the string should not be colored
      Assert.assertThat(shellTest.getStdOut(), containsString("public void"));
   }
}
