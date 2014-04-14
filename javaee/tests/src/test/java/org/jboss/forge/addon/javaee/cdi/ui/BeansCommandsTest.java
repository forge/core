/**
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.jboss.forge.addon.javaee.cdi.ui;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.hamcrest.CoreMatchers;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.forge.addon.javaee.ProjectHelper;
import org.jboss.forge.addon.javaee.cdi.CDIFacet_1_0;
import org.jboss.forge.addon.javaee.cdi.CDIFacet_1_1;
import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.shell.test.ShellTest;
import org.jboss.forge.arquillian.AddonDependency;
import org.jboss.forge.arquillian.Dependencies;
import org.jboss.forge.arquillian.archive.ForgeArchive;
import org.jboss.forge.furnace.repositories.AddonDependencyEntry;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="ggastald@redhat.com">George Gastaldi</a>
 */
@RunWith(Arquillian.class)
public class BeansCommandsTest
{
   @Deployment
   @Dependencies({
            @AddonDependency(name = "org.jboss.forge.addon:ui"),
            @AddonDependency(name = "org.jboss.forge.addon:shell-test-harness"),
            @AddonDependency(name = "org.jboss.forge.addon:javaee"),
            @AddonDependency(name = "org.jboss.forge.addon:projects"),
            @AddonDependency(name = "org.jboss.forge.addon:maven")
   })
   public static ForgeArchive getDeployment()
   {
      return ShrinkWrap
               .create(ForgeArchive.class)
               .addClass(ProjectHelper.class)
               .addBeansXML()
               .addAsAddonDependencies(
                        AddonDependencyEntry.create("org.jboss.forge.furnace.container:cdi"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:projects"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:javaee"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:maven"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:ui"),
                        AddonDependencyEntry.create("org.jboss.forge.addon:shell-test-harness")
               );
   }

   @Inject
   private ShellTest shellTest;

   @Inject
   private ProjectHelper projectHelper;

   @Before
   public void before() throws IOException
   {
      shellTest.clearScreen();
   }

   @Test
   public void testListAlternatives_1_1() throws Exception
   {
      Project project = projectHelper.createJavaLibraryProject();
      CDIFacet_1_1 cdiFacet = projectHelper.installCDI_1_1(project);
      org.jboss.shrinkwrap.descriptor.api.beans11.BeansDescriptor config = cdiFacet.getConfig();
      config.getOrCreateAlternatives().clazz("org.myclazz.MyAlternative");
      cdiFacet.saveConfig(config);

      shellTest.execute("cd " + project.getRoot().getFullyQualifiedName(), 10, TimeUnit.SECONDS);
      shellTest.execute("cdi-list-alternatives", 10, TimeUnit.SECONDS);
      Assert.assertThat(shellTest.getStdOut(), CoreMatchers.containsString("org.myclazz.MyAlternative"));
   }

   @Test
   public void testListAlternatives_1_0() throws Exception
   {
      Project project = projectHelper.createJavaLibraryProject();
      CDIFacet_1_0 cdiFacet = projectHelper.installCDI_1_0(project);
      org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor config = cdiFacet.getConfig();
      config.getOrCreateAlternatives().clazz("org.myclazz.MyAlternative");
      cdiFacet.saveConfig(config);

      shellTest.execute("cd " + project.getRoot().getFullyQualifiedName(), 10, TimeUnit.SECONDS);
      shellTest.execute("cdi-list-alternatives", 10, TimeUnit.SECONDS);
      Assert.assertThat(shellTest.getStdOut(), CoreMatchers.containsString("org.myclazz.MyAlternative"));
   }

   @Test
   public void testListDecorators_1_1() throws Exception
   {
      Project project = projectHelper.createJavaLibraryProject();
      CDIFacet_1_1 cdiFacet = projectHelper.installCDI_1_1(project);
      org.jboss.shrinkwrap.descriptor.api.beans11.BeansDescriptor config = cdiFacet.getConfig();
      config.getOrCreateDecorators().clazz("org.myclazz.MyDecorator");
      cdiFacet.saveConfig(config);

      shellTest.execute("cd " + project.getRoot().getFullyQualifiedName(), 10, TimeUnit.SECONDS);
      shellTest.execute("cdi-list-decorators", 10, TimeUnit.SECONDS);
      Assert.assertThat(shellTest.getStdOut(), CoreMatchers.containsString("org.myclazz.MyDecorator"));
   }

   @Test
   public void testListDecorators_1_0() throws Exception
   {
      Project project = projectHelper.createJavaLibraryProject();
      CDIFacet_1_0 cdiFacet = projectHelper.installCDI_1_0(project);
      org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor config = cdiFacet.getConfig();
      config.getOrCreateDecorators().clazz("org.myclazz.MyDecorator");
      cdiFacet.saveConfig(config);

      shellTest.execute("cd " + project.getRoot().getFullyQualifiedName(), 10, TimeUnit.SECONDS);
      shellTest.execute("cdi-list-decorators", 10, TimeUnit.SECONDS);
      Assert.assertThat(shellTest.getStdOut(), CoreMatchers.containsString("org.myclazz.MyDecorator"));
   }

   @Test
   public void testListInterceptors_1_1() throws Exception
   {
      Project project = projectHelper.createJavaLibraryProject();
      CDIFacet_1_1 cdiFacet = projectHelper.installCDI_1_1(project);
      org.jboss.shrinkwrap.descriptor.api.beans11.BeansDescriptor config = cdiFacet.getConfig();
      config.getOrCreateInterceptors().clazz("org.myclazz.MyInterceptor");
      cdiFacet.saveConfig(config);

      shellTest.execute("cd " + project.getRoot().getFullyQualifiedName(), 10, TimeUnit.SECONDS);
      shellTest.execute("cdi-list-interceptors", 10, TimeUnit.SECONDS);
      Assert.assertThat(shellTest.getStdOut(), CoreMatchers.containsString("org.myclazz.MyInterceptor"));
   }

   @Test
   public void testListInterceptors_1_0() throws Exception
   {
      Project project = projectHelper.createJavaLibraryProject();
      CDIFacet_1_0 cdiFacet = projectHelper.installCDI_1_0(project);
      org.jboss.shrinkwrap.descriptor.api.beans10.BeansDescriptor config = cdiFacet.getConfig();
      config.getOrCreateInterceptors().clazz("org.myclazz.MyInterceptor");
      cdiFacet.saveConfig(config);

      shellTest.execute("cd " + project.getRoot().getFullyQualifiedName(), 10, TimeUnit.SECONDS);
      shellTest.execute("cdi-list-interceptors", 10, TimeUnit.SECONDS);
      Assert.assertThat(shellTest.getStdOut(), CoreMatchers.containsString("org.myclazz.MyInterceptor"));
   }

}
