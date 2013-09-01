/*
 * Copyright 2012 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.shell.test.plugins.builtin;

import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.project.services.ResourceFactory;
import org.jboss.forge.resources.DirectoryResource;
import org.jboss.forge.shell.InstalledPluginRegistry;
import org.jboss.forge.shell.Shell;
import org.jboss.forge.shell.ShellImpl;
import org.jboss.forge.shell.plugins.builtin.ForgePlugin;
import org.jboss.forge.test.AbstractShellTest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:lincolnbaxter@gmail.com">Lincoln Baxter, III</a>
 */
@RunWith(Arquillian.class)
public class ForgePluginTest extends AbstractShellTest
{
   @Inject
   private ResourceFactory factory;

   @Before
   public void beforePluginTest()
   {
      getShell().getEnvironment().setProperty(ShellImpl.PROP_DEFAULT_PLUGIN_REPO, ShellImpl.DEFAULT_PLUGIN_REPO);
   }

   @Test
   @Ignore
   public void testFindPlugin() throws Exception
   {
      Shell shell = getShell();
      shell.execute("forge find-plugin jsf");
   }

   @Test
   public void testLogo() throws Exception
   {
      getShell().execute("forge");
   }

   @Test
   @Ignore
   public void testGitPluginNoProject() throws Exception
   {
      getShell().setCurrentResource(createTempFolder());
      getShell().execute("forge git-plugin git://github.com/forge/scaffold-aerogear.git");
   }

   @Test
   @Ignore
   public void testBuildPrettyfaces() throws Exception
   {
      getShell().getEnvironment().setProperty(ShellImpl.PROP_FORGE_VERSION, "1.0.3.Final");
      getShell().execute("forge install-plugin ocpsoft-prettyfaces");
   }

   @Test
   public void testListPlugins() throws Exception
   {
      InstalledPluginRegistry.install("test.test", "1.0.0-SNAPSHOT", "moo");
      getShell().execute("forge list-plugins arquillian");
      Assert.assertFalse(getOutput().contains("test.test"));

      getShell().execute("forge list-plugins test.test");
      Assert.assertTrue(getOutput().contains("test.test"));
   }

   @Test
   public void testListPluginsGrep() throws Exception
   {
      InstalledPluginRegistry.install("test.test", "1.0.0-SNAPSHOT", "moo");
      getShell().execute("forge list-plugins | grep arquillian");
      Assert.assertFalse(getOutput().contains("test.test"));

      getShell().execute("forge list-plugins | grep test.test");
      Assert.assertTrue(getOutput().contains("test.test"));
   }

   @Ignore("Not quite operational yet...")
   @Test
   public void validateInstallingForgePluginFromMultiModuleGitRepo() throws Exception
   {
       // Assemble
       final String groupId = "se.jguru.nazgul.forge.factory.impl.nazgul";
       final String artifactId = "nazgul-forge-factory-impl-nazgul";
       final String gitHubUrl = "https://github.com/lennartj/nazgul_forge.git";

       final Shell shell = getShell();
       shell.getEnvironment().setProperty(ShellImpl.PROP_FORGE_VERSION, "1.3.3.Final");

       // Act
       shell.execute("forge git-plugin " + gitHubUrl + " --groupId " + groupId + " --artifactId " + artifactId);

       // Assert
       final String output = getOutput();

       Assert.assertTrue(
               "Could not find artifactId [" + artifactId + "] in output [" + output + "]",
               output.contains(artifactId));
       Assert.assertTrue(
               "Could not find groupId [" + groupId + "] in output [" + output + "]",
               output.contains(groupId));
   }

   @Test
   public void validateFindingMatchingProject()
   {
       // Assemble
       final URL pointOfOrigin = getClass().getClassLoader().getResource("testdata/pointOfOrigin.txt");
       final File reactorRootDir = new File(new File(pointOfOrigin.getPath()).getParentFile(), "reactor");
       final DirectoryResource reactorRoot = factory.getResourceFrom(reactorRootDir).reify(DirectoryResource.class);

       final List<DirectoryResource> okResult = new ArrayList<DirectoryResource>();
       final List<DirectoryResource> emptyResult = new ArrayList<DirectoryResource>();

       // Act
       invokeAddIfMatching(okResult, "com.acme.foo.somecomponent", "foo-somecomponent-reactor", reactorRoot);
       invokeAddIfMatching(emptyResult, "com.acme.foo.somecomponent", "some-other-artifactId", reactorRoot);

       // Assert
       Assert.assertEquals(1, okResult.size());
       Assert.assertEquals(0, emptyResult.size());

       final DirectoryResource someComponentDirectory = okResult.get(0);
       Assert.assertEquals("somecomponent", someComponentDirectory.getName());
   }

    //
    // Private helpers
    //

    private void invokeAddIfMatching(final List<DirectoryResource> toPopulate,
                                     final String groupId,
                                     final String artifactId,
                                     final DirectoryResource currentDirectory) {

        final String methodName = "addIfMatching";

        try {
            final Method method = ForgePlugin.class.getDeclaredMethod(methodName,
                    List.class,
                    String.class,
                    String.class,
                    DirectoryResource.class);
            method.setAccessible(true);

            // Invoke reflectively.
            method.invoke(null, toPopulate, groupId, artifactId, currentDirectory);

        } catch (Exception e) {
            throw new IllegalArgumentException("Could not invoke method [" + methodName + "]", e);
        }
    }
}
